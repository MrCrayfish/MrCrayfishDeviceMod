package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Image extends Component
{
    public static final Map<String, CachedImage> CACHE = new HashMap<>();

    private Spinner spinner;

    protected ImageLoader loader;
    protected CachedImage image;
    protected boolean initialized = false;
    protected boolean drawFull = false;

    protected int imageU, imageV;
    protected int imageWidth, imageHeight;
    protected int componentWidth, componentHeight;

    private float alpha = 1.0F;

    private boolean hasBorder = false;
    private int borderColor = Color.BLACK.getRGB();
    private int borderThickness = 1;

    public Image(int left, int top, int width, int height)
    {
        super(left, top);
        this.componentWidth = width;
        this.componentHeight = height;
    }

    /**
     * Creates a new Image using a ResourceLocation. This automatically sets the width and height of
     * the component according to the width and height of the image.
     *
     * @param left        the amount of pixels to be offset from the left
     * @param top         the amount of pixels to be offset from the top
     * @param imageU      the u position on the image resource
     * @param imageV      the v position on the image resource
     * @param imageWidth  the image width
     * @param imageHeight the image height
     * @param resource    the resource location of the image
     */
    public Image(int left, int top, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource)
    {
        this(left, top, imageWidth, imageHeight, imageU, imageV, imageWidth, imageHeight, resource);
    }

    /**
     * Creates a new Image using a ResourceLocation. This constructor allows the specification of
     * the width and height of the component instead of automatically unlike
     * {@link Image#Image(int, int, int, int, int, int, ResourceLocation)}
     *
     * @param left            the amount of pixels to be offset from the left
     * @param top             the amount of pixels to be offset from the top
     * @param componentWidth  the width of the component
     * @param componentHeight the height of the component
     * @param imageU          the u position on the image resource
     * @param imageV          the v position on the image resource
     * @param imageWidth      the image width
     * @param imageHeight     the image height
     * @param resource        the resource location of the image
     */
    public Image(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource)
    {
        super(left, top);
        this.loader = new StandardLoader(resource);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = imageU;
        this.imageV = imageV;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * Creates a new Image from a url. This allows a resource to be downloaded from the internet
     * and be used as the Image. In the case that the resource could not be downloaded or the player
     * is playing the game in an offline state, the Image will default to a missing texture.
     * <p>
     * It should be noted that the remote resource is cached, so updating it may not result in an
     * instant change. Caching has a default limit of 10 resources but this can be changed by the
     * player in the configuration.
     *
     * @param left            the amount of pixels to be offset from the left
     * @param top             the amount of pixels to be offset from the top
     * @param componentWidth  the width of the component
     * @param componentHeight the height of the component
     * @param url             the url of the resource
     */
    public Image(int left, int top, int componentWidth, int componentHeight, String url)
    {
        super(left, top);
        this.loader = new DynamicLoader(url);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.drawFull = true;
    }

    public Image(int left, int top, IIcon icon)
    {
        super(left, top);
        this.loader = new StandardLoader(icon.getIconAsset());
        this.componentWidth = icon.getIconSize();
        this.componentHeight = icon.getIconSize();
        this.imageU = icon.getU();
        this.imageV = icon.getV();
        this.imageWidth = icon.getIconSize();
        this.imageHeight = icon.getIconSize();
    }

    public Image(int left, int top, int componentWidth, int componentHeight, IIcon icon)
    {
        super(left, top);
        this.loader = new StandardLoader(icon.getIconAsset());
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = icon.getU();
        this.imageV = icon.getV();
        this.imageWidth = icon.getIconSize();
        this.imageHeight = icon.getIconSize();
    }

    @Override
    public void init(Layout layout)
    {
        spinner = new Spinner(left + (componentWidth / 2) - 6, top + (componentHeight / 2) - 6);
        layout.addComponent(spinner);
        initialized = true;
    }

    @Override
    public void handleLoad()
    {
        if(loader != null)
        {
            loader.setup(this);
        }
    }

    @Override
    protected void handleUnload()
    {
        this.initialized = false;
    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        if(this.visible)
        {
            if(loader != null && loader.setup)
            {
                image = loader.load(this);
                spinner.setVisible(false);
                loader.setup = false;
            }

            if(hasBorder)
            {
                drawRect(x, y, x + componentWidth, y + componentHeight, borderColor);
            }

            if(image != null && image.textureId != -1)
            {
                image.restore();

                GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.bindTexture(image.textureId);

                if(hasBorder)
                {
                    if(drawFull)
                    {
                        RenderUtil.drawRectWithFullTexture(x + borderThickness, y + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2);
                    }
                    else
                    {
                        RenderUtil.drawRectWithTexture(x + borderThickness, y + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, imageWidth, imageHeight);
                    }
                }
                else
                {
                    if(drawFull)
                    {
                        RenderUtil.drawRectWithFullTexture(x, y, imageU, imageV, componentWidth, componentHeight);
                    }
                    else
                    {
                        RenderUtil.drawRectWithTexture(x, y, imageU, imageV, componentWidth, componentHeight, imageWidth, imageHeight);
                    }
                }
            }
            else
            {
                if(hasBorder)
                {
                    drawRect(x + borderThickness, y + borderThickness, x + componentWidth - borderThickness, y + componentHeight - borderThickness, Color.LIGHT_GRAY.getRGB());
                }
                else
                {
                    drawRect(x, y, x + componentWidth, y + componentHeight, Color.LIGHT_GRAY.getRGB());
                }
            }
        }
    }

    public void reload()
    {
        loader.setup(this);
    }

    public void setImage(ResourceLocation resource)
    {
        setLoader(new StandardLoader(resource));
        this.drawFull = true;
    }

    public void setImage(String url)
    {
        setLoader(new DynamicLoader(url));
        this.drawFull = true;
    }

    private void setLoader(ImageLoader loader)
    {
        this.loader = loader;
        if(initialized)
        {
            loader.setup(this);
            spinner.setVisible(true);
        }
    }

    /**
     * Sets the alpha for this image. Must be in the range
     * of 0.0F to 1.0F
     *
     * @param alpha how transparent you want it to be.
     */
    public void setAlpha(float alpha)
    {
        if(alpha < 0.0F)
        {
            this.alpha = 0.0F;
            return;
        }
        if(alpha > 1.0F)
        {
            this.alpha = 1.0F;
            return;
        }
        this.alpha = alpha;
    }

    /**
     * Makes it so the border shows
     *
     * @param show should the border show
     */
    public void setBorderVisible(boolean show)
    {
        this.hasBorder = show;
    }

    /**
     * Sets the border color for this component
     *
     * @param color the border color
     */
    private void setBorderColor(Color color)
    {
        this.borderColor = color.getRGB();
    }

    /**
     * Sets the thickness of the border
     *
     * @param thickness how thick in pixels
     */
    public void setBorderThickness(int thickness)
    {
        this.borderThickness = thickness;
    }

    public void setDrawFull(boolean drawFull)
    {
        this.drawFull = drawFull;
    }

    /**
     * Image Loader
     */
    private static abstract class ImageLoader
    {
        protected boolean setup = false;

        public final boolean isSetup()
        {
            return setup;
        }

        protected void setup(Image image)
        {
            setup = false;
        }

        public abstract CachedImage load(Image image);
    }

    private static class StandardLoader extends ImageLoader
    {
        private final AbstractTexture texture;
        private final String resource;

        public StandardLoader(ResourceLocation resource)
        {
            this.texture = new SimpleTexture(resource);
            this.resource = resource.toString();
        }

        @Override
        protected void setup(Image image)
        {
            setup = true;
        }

        @Override
        public CachedImage load(Image image)
        {
            if(CACHE.containsKey(resource))
            {
                return CACHE.get(resource);
            }

            try
            {
                ResourceLocation resourceLocation = new ResourceLocation(resource);
                ITextureObject textureObj = Minecraft.getMinecraft().getTextureManager().getTexture(resourceLocation);
                int textureId;
                if(textureObj != null)
                {
                    textureId = textureObj.getGlTextureId();
                }
                else
                {
                    texture.loadTexture(Minecraft.getMinecraft().getResourceManager());
                    textureId = texture.getGlTextureId();
                }
                CachedImage cachedImage = new CachedImage(textureId, 0, 0);
                CACHE.put(resource, cachedImage);
                return cachedImage;
            }
            catch(IOException e)
            {
                return new CachedImage(TextureUtil.MISSING_TEXTURE.getGlTextureId(), 0, 0);
            }
        }
    }

    private static class DynamicLoader extends ImageLoader
    {
        private AbstractTexture texture;
        private String url;

        public DynamicLoader(String url)
        {
            this.url = url;
        }

        @Override
        public void setup(final Image image)
        {
            if(CACHE.containsKey(url))
            {
                setup = true;
                return;
            }
            Runnable r = () ->
            {
                try
                {
                    URL url = new URL(this.url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedImage bufferedImage = ImageIO.read(conn.getInputStream());
                    System.out.println("Loaded image: " + this.url);
                    System.out.println(bufferedImage.getWidth() + " " + bufferedImage.getHeight());
                    image.imageWidth = bufferedImage.getWidth();
                    image.imageHeight = bufferedImage.getHeight();
                    texture = new DynamicTexture(bufferedImage);
                    setup = true;
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(r, "Image Loader");
            thread.start();
        }

        @Override
        public CachedImage load(Image image)
        {
            if(CACHE.containsKey(url))
            {
                CachedImage cachedImage = CACHE.get(url);
                image.imageWidth = cachedImage.width;
                image.imageHeight = cachedImage.height;
                return cachedImage;
            }

            try
            {
                texture.loadTexture(Minecraft.getMinecraft().getResourceManager());
                CachedImage cachedImage = new CachedImage(texture.getGlTextureId(), image.imageWidth, image.imageHeight);
                CACHE.put(url, cachedImage);
                return cachedImage;
            }
            catch(IOException e)
            {
                return new CachedImage(TextureUtil.MISSING_TEXTURE.getGlTextureId(), 0, 0);
            }
        }
    }

    private static class DynamicTexture extends AbstractTexture
    {
        private BufferedImage image;

        private DynamicTexture(BufferedImage image)
        {
            this.image = image;
        }

        @Override
        public void loadTexture(IResourceManager resourceManager) throws IOException
        {
            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, true);
        }
    }

    private static class ImageCache extends LinkedHashMap<String, CachedImage>
    {
        private final int CAPACITY;

        private ImageCache(final int capacity)
        {
            super(capacity, 1.0F, true);
            this.CAPACITY = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CachedImage> eldest)
        {
            if(size() > CAPACITY)
            {
                eldest.getValue().delete = true;
                return true;
            }
            return false;
        }
    }

    public static class CachedImage
    {
        private final int textureId;
        private final int width;
        private final int height;
        private boolean delete = false;

        private CachedImage(int textureId, int width, int height)
        {
            this.textureId = textureId;
            this.width = width;
            this.height = height;
        }

        public int getTextureId()
        {
            return textureId;
        }

        public void restore()
        {
            delete = false;
        }

        public void delete()
        {
            delete = true;
        }

        public boolean isPendingDeletion()
        {
            return delete;
        }
    }
}
