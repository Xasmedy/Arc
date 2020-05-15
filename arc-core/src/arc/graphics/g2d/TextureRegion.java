package arc.graphics.g2d;

import arc.*;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;

/**
 * Defines a rectangular area of a texture. The coordinate system used has its origin in the upper left corner with the x-axis
 * pointing to the right and the y axis pointing downwards.
 * @author mzechner
 * @author Nathan Sweet
 */
public class TextureRegion{
    Texture texture;
    float u, v;
    float u2, v2;
    int width, height;

    /** Constructs a region with no texture and no coordinates defined. */
    public TextureRegion(){
    }

    /** Constructs a region the size of the specified texture. */
    public TextureRegion(Texture texture){
        if(texture == null) throw new IllegalArgumentException("texture cannot be null.");
        this.texture = texture;
        set(0, 0, texture.getWidth(), texture.getHeight());
    }

    /**
     * @param width The width of the texture region. May be negative to flip the sprite when drawn.
     * @param height The height of the texture region. May be negative to flip the sprite when drawn.
     */
    public TextureRegion(Texture texture, int width, int height){
        this.texture = texture;
        set(0, 0, width, height);
    }

    /**
     * @param width The width of the texture region. May be negative to flip the sprite when drawn.
     * @param height The height of the texture region. May be negative to flip the sprite when drawn.
     */
    public TextureRegion(Texture texture, int x, int y, int width, int height){
        this.texture = texture;
        set(x, y, width, height);
    }

    public TextureRegion(Texture texture, float u, float v, float u2, float v2){
        this.texture = texture;
        set(u, v, u2, v2);
    }

    /** Constructs a region with the same texture and coordinates of the specified region. */
    public TextureRegion(TextureRegion region){
        set(region);
    }

    /**
     * Constructs a region with the same texture as the specified region and sets the coordinates relative to the specified region.
     * @param width The width of the texture region. May be negative to flip the sprite when drawn.
     * @param height The height of the texture region. May be negative to flip the sprite when drawn.
     */
    public TextureRegion(TextureRegion region, int x, int y, int width, int height){
        set(region, x, y, width, height);
    }

    /**
     * Helper function to create tiles out of the given {@link Texture} starting from the top left corner going to the right and
     * ending at the bottom right corner. Only complete tiles will be returned so if the texture's width or height are not a
     * multiple of the tile width and height not all of the texture will be used.
     * @param texture the Texture
     * @param tileWidth a tile's width in pixels
     * @param tileHeight a tile's height in pixels
     * @return a 2D array of TextureRegions indexed by [row][column].
     */
    public static TextureRegion[][] split(Texture texture, int tileWidth, int tileHeight){
        TextureRegion region = new TextureRegion(texture);
        return region.split(tileWidth, tileHeight);
    }

    public AtlasRegion asAtlas(){
        return (AtlasRegion)this;
    }

    public boolean found(){
        return Core.atlas != null && Core.atlas.error != this;
    }

    /** Sets the texture and sets the coordinates to the size of the specified texture. */
    public void set(Texture texture){
        this.texture = texture;
        set(0, 0, texture.getWidth(), texture.getHeight());
    }

    /**
     * @param width The width of the texture region. May be negative to flip the sprite when drawn.
     * @param height The height of the texture region. May be negative to flip the sprite when drawn.
     */
    public void set(int x, int y, int width, int height){
        float invTexWidth = 1f / texture.getWidth();
        float invTexHeight = 1f / texture.getHeight();
        set(x * invTexWidth, y * invTexHeight, (x + width) * invTexWidth, (y + height) * invTexHeight);
        this.width = Math.abs(width);
        this.height = Math.abs(height);
    }

    public void set(float u, float v, float u2, float v2){
        int texWidth = texture.getWidth(), texHeight = texture.getHeight();
        width = Math.round(Math.abs(u2 - u) * texWidth);
        height = Math.round(Math.abs(v2 - v) * texHeight);

        // For a 1x1 region, adjust UVs toward pixel center to avoid filtering artifacts on AMD GPUs when drawing very stretched.
        if(width == 1 && height == 1){
            float adjustX = 0.25f / texWidth;
            u += adjustX;
            u2 -= adjustX;
            float adjustY = 0.25f / texHeight;
            v += adjustY;
            v2 -= adjustY;
        }

        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
    }

    /** Sets the texture and coordinates to the specified region. */
    public void set(TextureRegion region){
        texture = region.texture;
        set(region.u, region.v, region.u2, region.v2);
    }

    /** Sets the texture to that of the specified region and sets the coordinates relative to the specified region. */
    public void set(TextureRegion region, int x, int y, int width, int height){
        texture = region.texture;
        set(region.getX() + x, region.getY() + y, width, height);
    }

    /** Sets the texture to that of the specified region and sets the coordinates relative to the specified region. */
    public void set(Texture texture, int x, int y, int width, int height){
        this.texture = texture;
        set(x, y, width, height);
    }

    public Texture getTexture(){
        return texture;
    }

    public void setTexture(Texture texture){
        this.texture = texture;
    }

    public float getU(){
        return u;
    }

    public void setU(float u){
        this.u = u;
        width = Math.round(Math.abs(u2 - u) * texture.getWidth());
    }

    public float getV(){
        return v;
    }

    public void setV(float v){
        this.v = v;
        height = Math.round(Math.abs(v2 - v) * texture.getHeight());
    }

    public float getU2(){
        return u2;
    }

    public void setU2(float u2){
        this.u2 = u2;
        width = Math.round(Math.abs(u2 - u) * texture.getWidth());
    }

    public float getV2(){
        return v2;
    }

    public void setV2(float v2){
        this.v2 = v2;
        height = Math.round(Math.abs(v2 - v) * texture.getHeight());
    }

    public int getX(){
        return Math.round(u * texture.getWidth());
    }

    public void setX(int x){
        setU(x / (float)texture.getWidth());
    }

    public int getY(){
        return Math.round(v * texture.getHeight());
    }

    public void setY(int y){
        setV(y / (float)texture.getHeight());
    }

    /** Returns the region's width. */
    public int getWidth(){
        return width;
    }

    public void setWidth(int width){
        if(isFlipX()){
            setU(u2 + width / (float)texture.getWidth());
        }else{
            setU2(u + width / (float)texture.getWidth());
        }
    }

    /** Returns the region's height. */
    public int getHeight(){
        return height;
    }

    public void setHeight(int height){
        if(isFlipY()){
            setV(v2 + height / (float)texture.getHeight());
        }else{
            setV2(v + height / (float)texture.getHeight());
        }
    }

    public void flip(boolean x, boolean y){
        if(x){
            float temp = u;
            u = u2;
            u2 = temp;
        }
        if(y){
            float temp = v;
            v = v2;
            v2 = temp;
        }
    }

    public boolean isFlipX(){
        return u > u2;
    }

    public boolean isFlipY(){
        return v > v2;
    }

    /**
     * Offsets the region relative to the current region. Generally the region's size should be the entire size of the texture in
     * the direction(s) it is scrolled.
     * @param xAmount The percentage to offset horizontally.
     * @param yAmount The percentage to offset vertically. This is done in texture space, so up is negative.
     */
    public void scroll(float xAmount, float yAmount){
        if(xAmount != 0){
            float width = (u2 - u) * texture.getWidth();
            u = (u + xAmount) % 1;
            u2 = u + width / texture.getWidth();
        }
        if(yAmount != 0){
            float height = (v2 - v) * texture.getHeight();
            v = (v + yAmount) % 1;
            v2 = v + height / texture.getHeight();
        }
    }

    /**
     * Helper function to create tiles out of this TextureRegion starting from the top left corner going to the right and ending at
     * the bottom right corner. Only complete tiles will be returned so if the region's width or height are not a multiple of the
     * tile width and height not all of the region will be used. This will not work on texture regions returned form a TextureAtlas
     * that either have whitespace removed or where flipped before the region is split.
     * @param tileWidth a tile's width in pixels
     * @param tileHeight a tile's height in pixels
     * @return a 2D array of TextureRegions indexed by [row][column].
     */
    public TextureRegion[][] split(int tileWidth, int tileHeight){
        if(texture == null) return null;
        int x = getX();
        int y = getY();
        int width = this.width;
        int height = this.height;

        int sw = width / tileWidth;
        int sh = height / tileHeight;

        int startX = x;
        TextureRegion[][] tiles = new TextureRegion[sw][sh];
        for(int cy = 0; cy < sh; cy++, y += tileHeight){
            x = startX;
            for(int cx = 0; cx < sw; cx++, x += tileWidth){
                tiles[cx][cy] = new TextureRegion(texture, x, y, tileWidth, tileHeight);
            }
        }

        return tiles;
    }

    @Override
    public String toString(){
        return "TextureRegion{" +
        "texture=" + texture +
        ", width=" + width +
        ", height=" + height +
        '}';
    }
}
