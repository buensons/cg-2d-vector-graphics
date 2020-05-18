# 2D Vector Graphics Editor

Simple 2D graphics editor, which implements the following algorithms:

- Line drawing: Bresenham's symmetric midpoint line
- Line thickness: Copying pixels
- Circle: Midpoint circle
- Rectangles and polygons: just a sequence of Bresenham's lines

Clipping and Filling:

- Polygon clipping: Sutherland-Hodgman algorithm<br/>
- Polygon filling: Scanline with Active Edge Table<br/>
- General filling: 4-way boundary fill algorithm (not integrated yet)

![alt text](https://raw.githubusercontent.com/buensons/cg-2d-vector-graphics/master/images/main.png)

# Polygon Clipping (Sutherland-Hodgman algorithm)

Before:
![alt text](https://raw.githubusercontent.com/buensons/cg-2d-vector-graphics/master/images/before_clip.png)

After:
![alt text](https://raw.githubusercontent.com/buensons/cg-2d-vector-graphics/master/images/after_clip.png)

# Filling with patterns:

![alt text](https://raw.githubusercontent.com/buensons/cg-2d-vector-graphics/master/images/patterns.png)
