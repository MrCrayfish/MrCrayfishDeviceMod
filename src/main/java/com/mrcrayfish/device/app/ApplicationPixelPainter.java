package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ButtonToggle;
import com.mrcrayfish.device.app.components.CheckBox;
import com.mrcrayfish.device.app.components.RadioGroup;
import com.mrcrayfish.device.app.components.Slider;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.app.listener.SlideListener;
import com.mrcrayfish.device.object.Canvas;
import com.mrcrayfish.device.object.ColourGrid;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApplicationPixelPainter extends Application
{
	private static final ResourceLocation PIXEL_PAINTER_ICONS = new ResourceLocation("cdm:textures/gui/pixel_painter.png");
	
	private Canvas canvas;
	
	private ButtonToggle btnPencil;
	private ButtonToggle btnBucket;
	private ButtonToggle btnEraser;
	private ButtonToggle btnEyeDropper;
	private Button btnSave;
	
	private Slider redSlider;
	private Slider greenSlider;
	private Slider blueSlider;
	
	private Component colourDisplay;
	private ColourGrid colourGrid;
	
	private CheckBox displayGrid;
	
	public ApplicationPixelPainter() 
	{
		super("pixel_painter", "Pixel Painter", 213, 140);
	}
	
	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		this.canvas = new Canvas(x, y, 5, 5, 16, 16, 8, 8);
		super.addComponent(canvas);
		
		RadioGroup toolGroup = new RadioGroup();
		
		this.btnPencil = new ButtonToggle(x, y, 138, 5, PIXEL_PAINTER_ICONS, 0, 0, 10, 10);
		this.btnPencil.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setCurrentTool(Canvas.PENCIL);
			}
		});
		this.btnPencil.setRadioGroup(toolGroup);
		super.addComponent(btnPencil);
		
		this.btnBucket = new ButtonToggle(x, y, 138, 24, PIXEL_PAINTER_ICONS, 10, 0, 10, 10);
		this.btnBucket.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setCurrentTool(Canvas.BUCKET);
			}
		});
		this.btnBucket.setRadioGroup(toolGroup);
		super.addComponent(btnBucket);
		
		this.btnEraser = new ButtonToggle(x, y, 138, 43, PIXEL_PAINTER_ICONS, 20, 0, 10, 10);
		this.btnEraser.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setCurrentTool(Canvas.ERASER);
			}
		});
		this.btnEraser.setRadioGroup(toolGroup);
		super.addComponent(btnEraser);
		
		this.btnEyeDropper = new ButtonToggle(x, y, 138, 62, PIXEL_PAINTER_ICONS, 30, 0, 10, 10);
		this.btnEyeDropper.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setCurrentTool(Canvas.EYE_DROPPER);
				Color color = new Color(canvas.getCurrentColour());
				redSlider.setPercentage(color.getRed() / 255F);
				greenSlider.setPercentage(color.getGreen() / 255F);
				blueSlider.setPercentage(color.getBlue() / 255F);
			}
		});
		this.btnEyeDropper.setRadioGroup(toolGroup);
		super.addComponent(btnEyeDropper);
		
		this.btnSave = new Button(x, y, 138, 119, PIXEL_PAINTER_ICONS, 40, 0, 10, 10);
		super.addComponent(btnSave);
		
		this.redSlider = new Slider(x, y, 158, 30, 50);
		this.redSlider.setSlideListener(new SlideListener() {
			@Override
			public void onSlide(float percentage) {
				canvas.setRed(percentage);
			}
		});
		super.addComponent(redSlider);
		
		this.greenSlider = new Slider(x, y, 158, 46, 50);
		this.greenSlider.setSlideListener(new SlideListener() {
			@Override
			public void onSlide(float percentage) {
				canvas.setGreen(percentage);
			}
		});
		super.addComponent(greenSlider);
		
		this.blueSlider = new Slider(x, y, 158, 62, 50);
		this.blueSlider.setSlideListener(new SlideListener() {
			@Override
			public void onSlide(float percentage) {
				canvas.setBlue(percentage);
			}
		});
		super.addComponent(blueSlider);
		
		this.colourDisplay = new Component(x, y, 158, 5) {
			@Override
			public void render(Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
				drawRect(xPosition, yPosition, xPosition + 50, yPosition + 20, Color.DARK_GRAY.getRGB());
				drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 19, canvas.getCurrentColour());
			}
		};
		super.addComponent(colourDisplay);
		
		this.colourGrid = new ColourGrid(x, y, 157, 82, 50, canvas, redSlider, greenSlider, blueSlider);
		super.addComponent(colourGrid);
		
		this.displayGrid = new CheckBox("Grid", x, y, 166, 120);
		this.displayGrid.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setShowGrid(displayGrid.isSelected());
			}
		});
		super.addComponent(displayGrid);
	}

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}

}
