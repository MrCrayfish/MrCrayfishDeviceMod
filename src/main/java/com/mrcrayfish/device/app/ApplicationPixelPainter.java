package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ButtonToggle;
import com.mrcrayfish.device.app.components.CheckBox;
import com.mrcrayfish.device.app.components.Image;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.components.Label;
import com.mrcrayfish.device.app.components.RadioGroup;
import com.mrcrayfish.device.app.components.Slider;
import com.mrcrayfish.device.app.components.TextArea;
import com.mrcrayfish.device.app.components.TextField;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.app.listener.SlideListener;
import com.mrcrayfish.device.object.Canvas;
import com.mrcrayfish.device.object.ColourGrid;
import com.mrcrayfish.device.object.Note;
import com.mrcrayfish.device.object.Picture;
import com.mrcrayfish.device.object.Picture.Size;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class ApplicationPixelPainter extends Application
{
	private static final ResourceLocation PIXEL_PAINTER_ICONS = new ResourceLocation("cdm:textures/gui/pixel_painter.png");
	
	/* Main Menu */
	private Layout layoutMainMenu;
	private Image logo;
	private Button btnNewPicture;
	private Button btnLoadPicture;
	
	/* New Picture */
	private Layout layoutNewPicture;
	private Label labelName;
	private TextField fieldName;
	private Label labelAuthor;
	private TextField fieldAuthor;
	private Label labelSize;
	private CheckBox checkBox16x;
	private CheckBox checkBox32x;
	private Button btnCreatePicture;
	
	/* Load Picture */
	private Layout layoutLoadPicture;
	private Label labelPictureList;
	private ItemList<Picture> listPictures;
	private Button btnLoadSavedPicture;
	private Button btnDeleteSavedPicture;
	private Button btnBackSavedPicture;
	
	/* Drawing */
	private Layout layoutDraw;
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
		super("pixel_painter", "Pixel Painter");
	}
	
	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		
		/* Main Menu */
		this.layoutMainMenu = new Layout(100, 100);
		
		this.btnNewPicture = new Button("New", x, y, 5, 50, 90, 20);
		this.btnNewPicture.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				ApplicationPixelPainter.this.setCurrentLayout(layoutNewPicture);
			}
		});
		this.layoutMainMenu.addComponent(btnNewPicture);
		
		this.btnLoadPicture = new Button("Load", x, y, 5, 75, 90, 20);
		this.btnLoadPicture.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				ApplicationPixelPainter.this.setCurrentLayout(layoutLoadPicture);
			}
		});
		this.layoutMainMenu.addComponent(btnLoadPicture);
		
		/* New Picture */
		this.layoutNewPicture = new Layout(180, 65);
		
		this.labelName = new Label("Name", x, y, 5, 5);
		this.layoutNewPicture.addComponent(labelName);
		
		this.fieldName = new TextField(Minecraft.getMinecraft().fontRendererObj, x, y, 5, 15, 100);
		this.layoutNewPicture.addComponent(fieldName);
		
		this.labelAuthor = new Label("Author", x, y, 5, 35);
		this.layoutNewPicture.addComponent(labelAuthor);
		
		this.fieldAuthor = new TextField(Minecraft.getMinecraft().fontRendererObj, x, y, 5, 45, 100);
		this.layoutNewPicture.addComponent(fieldAuthor);
		
		this.labelSize = new Label("Size", x, y, 110, 5);
		this.layoutNewPicture.addComponent(labelSize);
		
		RadioGroup sizeGroup = new RadioGroup();
		
		this.checkBox16x = new CheckBox("16x", x, y, 110, 17);
		this.checkBox16x.setSelected(true);
		this.checkBox16x.setRadioGroup(sizeGroup);
		this.layoutNewPicture.addComponent(checkBox16x);
		
		this.checkBox32x = new CheckBox("32x", x, y, 145, 17);
		this.checkBox32x.setRadioGroup(sizeGroup);
		this.layoutNewPicture.addComponent(checkBox32x);
		
		this.btnCreatePicture = new Button("Create", x, y, 110, 40, 65, 20);
		this.btnCreatePicture.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				ApplicationPixelPainter.this.setCurrentLayout(layoutDraw);
				canvas.setPicture(new Picture(fieldName.getText(), fieldAuthor.getText(), checkBox16x.isSelected() ? Size.X16 : Size.X32));
			}
		});
		this.layoutNewPicture.addComponent(btnCreatePicture);
		
		/* Load Picture */
		this.layoutLoadPicture = new Layout(180, 80);
		
		this.listPictures = new ItemList<Picture>(x, y, 5, 5, 100, 5);
		this.layoutLoadPicture.addComponent(listPictures);
		
		this.btnLoadSavedPicture = new Button("Load", x, y, 124, 5, 50, 20);
		this.btnLoadSavedPicture.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				if(listPictures.getSelectedIndex() != -1)
				{
					canvas.setPicture(listPictures.getSelectedItem());
					ApplicationPixelPainter.this.setCurrentLayout(layoutDraw);
				}
			}
		});
		this.layoutLoadPicture.addComponent(btnLoadSavedPicture);
		
		this.btnDeleteSavedPicture = new Button("Delete", x, y, 124, 30, 50, 20);
		this.btnDeleteSavedPicture.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				listPictures.removeItem(listPictures.getSelectedIndex());
			}
		});
		this.layoutLoadPicture.addComponent(btnDeleteSavedPicture);
		
		this.btnBackSavedPicture = new Button("Back", x, y, 124, 55, 50, 20);
		this.btnBackSavedPicture.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				ApplicationPixelPainter.this.setCurrentLayout(layoutMainMenu);
			}
		});
		this.layoutLoadPicture.addComponent(btnBackSavedPicture);
		
		/* Drawing */
		this.layoutDraw = new Layout(213, 140);
		
		this.canvas = new Canvas(x, y, 5, 5);
		this.layoutDraw.addComponent(canvas);
		
		RadioGroup toolGroup = new RadioGroup();
		
		this.btnPencil = new ButtonToggle(x, y, 138, 5, PIXEL_PAINTER_ICONS, 0, 0, 10, 10);
		this.btnPencil.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setCurrentTool(Canvas.PENCIL);
			}
		});
		this.btnPencil.setRadioGroup(toolGroup);
		this.layoutDraw.addComponent(btnPencil);
		
		this.btnBucket = new ButtonToggle(x, y, 138, 24, PIXEL_PAINTER_ICONS, 10, 0, 10, 10);
		this.btnBucket.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setCurrentTool(Canvas.BUCKET);
			}
		});
		this.btnBucket.setRadioGroup(toolGroup);
		this.layoutDraw.addComponent(btnBucket);
		
		this.btnEraser = new ButtonToggle(x, y, 138, 43, PIXEL_PAINTER_ICONS, 20, 0, 10, 10);
		this.btnEraser.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setCurrentTool(Canvas.ERASER);
			}
		});
		this.btnEraser.setRadioGroup(toolGroup);
		this.layoutDraw.addComponent(btnEraser);
		
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
		this.layoutDraw.addComponent(btnEyeDropper);
		
		this.btnSave = new Button(x, y, 138, 119, PIXEL_PAINTER_ICONS, 40, 0, 10, 10);
		this.btnSave.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				listPictures.addItem(canvas.picture);
				ApplicationPixelPainter.this.setCurrentLayout(layoutLoadPicture);
				ApplicationPixelPainter.this.markDirty();
			}
		});
		this.layoutDraw.addComponent(btnSave);
		
		this.redSlider = new Slider(x, y, 158, 30, 50);
		this.redSlider.setSlideListener(new SlideListener() {
			@Override
			public void onSlide(float percentage) {
				canvas.setRed(percentage);
			}
		});
		this.layoutDraw.addComponent(redSlider);
		
		this.greenSlider = new Slider(x, y, 158, 46, 50);
		this.greenSlider.setSlideListener(new SlideListener() {
			@Override
			public void onSlide(float percentage) {
				canvas.setGreen(percentage);
			}
		});
		this.layoutDraw.addComponent(greenSlider);
		
		this.blueSlider = new Slider(x, y, 158, 62, 50);
		this.blueSlider.setSlideListener(new SlideListener() {
			@Override
			public void onSlide(float percentage) {
				canvas.setBlue(percentage);
			}
		});
		this.layoutDraw.addComponent(blueSlider);
		
		this.colourDisplay = new Component(x, y, 158, 5) {
			@Override
			public void render(Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
				drawRect(xPosition, yPosition, xPosition + 50, yPosition + 20, Color.DARK_GRAY.getRGB());
				drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 19, canvas.getCurrentColour());
			}
		};
		this.layoutDraw.addComponent(colourDisplay);
		
		this.colourGrid = new ColourGrid(x, y, 157, 82, 50, canvas, redSlider, greenSlider, blueSlider);
		this.layoutDraw.addComponent(colourGrid);
		
		this.displayGrid = new CheckBox("Grid", x, y, 166, 120);
		this.displayGrid.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				canvas.setShowGrid(displayGrid.isSelected());
			}
		});
		this.layoutDraw.addComponent(displayGrid);
		
		this.setCurrentLayout(layoutMainMenu);
	}

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		listPictures.removeAll();
		
		NBTTagList pictureList = (NBTTagList) tagCompound.getTag("Pictures");
		for(int i = 0; i < pictureList.tagCount(); i++)
		{
			NBTTagCompound pictureTag = pictureList.getCompoundTagAt(i);
			Picture picture = Picture.readFromNBT(pictureTag);
			listPictures.addItem(picture);
			System.out.println("Loading picture: " + picture.getName());
		}
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		NBTTagList pictureList = new NBTTagList();
		for(Picture picture : listPictures)
		{
			NBTTagCompound pictureTag = new NBTTagCompound();
			picture.writeToNBT(pictureTag);
			pictureList.appendTag(pictureTag);
			System.out.println("Saving picture: " + picture.getName());
		}
		tagCompound.setTag("Pictures", pictureList);
	}
}
