#!/usr/bin/env python3
"""Report how much actual image a screenshot contains.

Used by the emulator smoke test: a process can be alive, render frames and still
show a completely black screen. This prints

    distinct=<number of distinct quantised colours>
    bright=<percentage of pixels that are not near-black>
    avg=<mean luminance 0-255>

The values are deliberately coarse so the check is stable across GPU drivers.
Uses only the standard library plus zlib (PNG is decoded by hand) because the
CI image does not guarantee Pillow or ImageMagick.
"""

import struct
import sys
import zlib


def load_png(path):
    """Return (width, height, rows) where rows are RGB/RGBA bytes per pixel."""
    with open(path, "rb") as handle:
        data = handle.read()

    if not data.startswith(b"\x89PNG\r\n\x1a\n"):
        raise SystemExit("not a PNG")

    pos = 8
    width = height = None
    bit_depth = colour_type = None
    idat = bytearray()

    while pos < len(data):
        (length,) = struct.unpack(">I", data[pos : pos + 4])
        chunk_type = data[pos + 4 : pos + 8]
        chunk_data = data[pos + 8 : pos + 8 + length]
        pos += 12 + length

        if chunk_type == b"IHDR":
            width, height, bit_depth, colour_type = struct.unpack(">IIBB", chunk_data[:10])
        elif chunk_type == b"IDAT":
            idat.extend(chunk_data)
        elif chunk_type == b"IEND":
            break

    if bit_depth != 8 or colour_type not in (2, 6):
        raise SystemExit("unsupported PNG format (bit depth %s, colour type %s)" % (bit_depth, colour_type))

    channels = 3 if colour_type == 2 else 4
    raw = zlib.decompress(bytes(idat))
    stride = width * channels

    rows = []
    previous = bytearray(stride)
    offset = 0
    for _ in range(height):
        filter_type = raw[offset]
        offset += 1
        line = bytearray(raw[offset : offset + stride])
        offset += stride

        if filter_type == 1:  # Sub
            for i in range(channels, stride):
                line[i] = (line[i] + line[i - channels]) & 0xFF
        elif filter_type == 2:  # Up
            for i in range(stride):
                line[i] = (line[i] + previous[i]) & 0xFF
        elif filter_type == 3:  # Average
            for i in range(stride):
                left = line[i - channels] if i >= channels else 0
                line[i] = (line[i] + ((left + previous[i]) >> 1)) & 0xFF
        elif filter_type == 4:  # Paeth
            for i in range(stride):
                a = line[i - channels] if i >= channels else 0
                b = previous[i]
                c = previous[i - channels] if i >= channels else 0
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                if pa <= pb and pa <= pc:
                    pred = a
                elif pb <= pc:
                    pred = b
                else:
                    pred = c
                line[i] = (line[i] + pred) & 0xFF
        elif filter_type != 0:
            raise SystemExit("unsupported filter %s" % filter_type)

        rows.append(bytes(line))
        previous = line

    return width, height, channels, rows


def main():
    if len(sys.argv) != 2:
        raise SystemExit("usage: screenshot_colours.py <png>")

    width, height, channels, rows = load_png(sys.argv[1])

    # Sample on a grid: a 1472x720 screenshot has ~1M pixels and the checker
    # only needs a statistical read.
    step = max(1, min(width, height) // 180)

    colours = set()
    total = 0
    non_black = 0
    luminance_sum = 0

    for y in range(0, height, step):
        row = rows[y]
        for x in range(0, width, step):
            base = x * channels
            r = row[base]
            g = row[base + 1]
            b = row[base + 2]
            total += 1
            luminance_sum += (r * 299 + g * 587 + b * 114) // 1000
            if r + g + b > 36:
                non_black += 1
            # Quantise to 5 bits per channel so gradients do not explode the count.
            colours.add(((r >> 3) << 10) | ((g >> 3) << 5) | (b >> 3))

    if total == 0:
        raise SystemExit("empty image")

    print("distinct=%d" % len(colours))
    print("bright=%d" % (100 * non_black // total))
    print("avg=%d" % (luminance_sum // total))


if __name__ == "__main__":
    main()
