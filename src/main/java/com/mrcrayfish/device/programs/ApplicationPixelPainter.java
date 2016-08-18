package com.mrcrayfish.device.programs;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ButtonToggle;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.SlideListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.TaskBar;
import com.mrcrayfish.device.object.Canvas;
import com.mrcrayfish.device.object.ColourGrid;
import com.mrcrayfish.device.object.Picture;
import com.mrcrayfish.device.object.Picture.Size;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class ApplicationPixelPainter extends Application
{
	private static final ResourceLocation PIXEL_PAINTER_ICONS = new ResourceLocation("cdm:textures/gui/pixel_painter.png");

	/* Main Menu */
	private Layout layoutMainMenu;
	private Image logo;
	private Label labelLogo;
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
	private Button btnCancel;
	private Button btnSave;
	private Slider redSlider;
	private Slider greenSlider;
	private Slider blueSlider;
	private Component colourDisplay;
	private ColourGrid colourGrid;
	private CheckBox displayGrid;

	public ApplicationPixelPainter()
	{
		super("pixel_painter", "Pixel Painter", TaskBar.APP_BAR_GUI, 56, 30);
	}

	@Override
	public void init(int x, int y)
	{
		super.init(x, y);

		/* Main Menu */
		
		layoutMainMenu = new Layout(100, 100);

		logo = new Image(x, y, 35, 5, 28, 28, u, v, 14, 14, icon);
		layoutMainMenu.addComponent(logo);

		labelLogo = new Label("Pixel Painter", x, y, 19, 35);
		layoutMainMenu.addComponent(labelLogo);

		btnNewPicture = new Button("New", x, y, 5, 50, 90, 20);
		btnNewPicture.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutNewPicture);
			}
		});
		layoutMainMenu.addComponent(btnNewPicture);

		btnLoadPicture = new Button("Load", x, y, 5, 75, 90, 20);
		btnLoadPicture.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutLoadPicture);
			}
		});
		layoutMainMenu.addComponent(btnLoadPicture);

		
		/* New Picture */
		
		layoutNewPicture = new Layout(180, 65);

		labelName = new Label("Name", x, y, 5, 5);
		layoutNewPicture.addComponent(labelName);

		fieldName = new TextField(x, y, 5, 15, 100);
		layoutNewPicture.addComponent(fieldName);

		labelAuthor = new Label("Author", x, y, 5, 35);
		layoutNewPicture.addComponent(labelAuthor);

		fieldAuthor = new TextField(x, y, 5, 45, 100);
		layoutNewPicture.addComponent(fieldAuthor);

		labelSize = new Label("Size", x, y, 110, 5);
		layoutNewPicture.addComponent(labelSize);

		RadioGroup sizeGroup = new RadioGroup();

		checkBox16x = new CheckBox("16x", x, y, 110, 17);
		checkBox16x.setSelected(true);
		checkBox16x.setRadioGroup(sizeGroup);
		layoutNewPicture.addComponent(checkBox16x);

		checkBox32x = new CheckBox("32x", x, y, 145, 17);
		checkBox32x.setRadioGroup(sizeGroup);
		layoutNewPicture.addComponent(checkBox32x);

		btnCreatePicture = new Button("Create", x, y, 110, 40, 65, 20);
		btnCreatePicture.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutDraw);
				canvas.createPicture(fieldName.getText(), fieldAuthor.getText(), checkBox16x.isSelected() ? Size.X16 : Size.X32);
			}
		});
		layoutNewPicture.addComponent(btnCreatePicture);

		
		/* Load Picture */
		
		layoutLoadPicture = new Layout(180, 80);

		listPictures = new ItemList<Picture>(x, y, 5, 5, 100, 5);
		layoutLoadPicture.addComponent(listPictures);

		btnLoadSavedPicture = new Button("Load", x, y, 124, 5, 50, 20);
		btnLoadSavedPicture.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if (listPictures.getSelectedIndex() != -1)
				{
					canvas.setPicture(listPictures.getSelectedItem());
					setCurrentLayout(layoutDraw);
				}
			}
		});
		layoutLoadPicture.addComponent(btnLoadSavedPicture);

		btnDeleteSavedPicture = new Button("Delete", x, y, 124, 30, 50, 20);
		btnDeleteSavedPicture.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				listPictures.removeItem(listPictures.getSelectedIndex());
			}
		});
		layoutLoadPicture.addComponent(btnDeleteSavedPicture);

		btnBackSavedPicture = new Button("Back", x, y, 124, 55, 50, 20);
		btnBackSavedPicture.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutMainMenu);
			}
		});
		layoutLoadPicture.addComponent(btnBackSavedPicture);

		
		/* Drawing */
		
		layoutDraw = new Layout(213, 140);

		canvas = new Canvas(x, y, 5, 5);
		layoutDraw.addComponent(canvas);

		RadioGroup toolGroup = new RadioGroup();

		btnPencil = new ButtonToggle(x, y, 138, 5, PIXEL_PAINTER_ICONS, 0, 0, 10, 10);
		btnPencil.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				canvas.setCurrentTool(Canvas.PENCIL);
			}
		});
		btnPencil.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnPencil);

		btnBucket = new ButtonToggle(x, y, 138, 24, PIXEL_PAINTER_ICONS, 10, 0, 10, 10);
		btnBucket.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				canvas.setCurrentTool(Canvas.BUCKET);
			}
		});
		btnBucket.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnBucket);

		btnEraser = new ButtonToggle(x, y, 138, 43, PIXEL_PAINTER_ICONS, 20, 0, 10, 10);
		btnEraser.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				canvas.setCurrentTool(Canvas.ERASER);
			}
		});
		btnEraser.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnEraser);

		btnEyeDropper = new ButtonToggle(x, y, 138, 62, PIXEL_PAINTER_ICONS, 30, 0, 10, 10);
		btnEyeDropper.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				canvas.setCurrentTool(Canvas.EYE_DROPPER);
				Color color = new Color(canvas.getCurrentColour());
				redSlider.setPercentage(color.getRed() / 255F);
				greenSlider.setPercentage(color.getGreen() / 255F);
				blueSlider.setPercentage(color.getBlue() / 255F);
			}
		});
		btnEyeDropper.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnEyeDropper);

		btnCancel = new Button(x, y, 138, 100, PIXEL_PAINTER_ICONS, 50, 0, 10, 10);
		btnCancel.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if (canvas.isExistingImage())
					setCurrentLayout(layoutLoadPicture);
				else
					setCurrentLayout(layoutMainMenu);
				canvas.clear();
			}
		});
		layoutDraw.addComponent(btnCancel);

		btnSave = new Button(x, y, 138, 119, PIXEL_PAINTER_ICONS, 40, 0, 10, 10);
		btnSave.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				canvas.picture.pixels = canvas.copyPixels();
				if (!canvas.isExistingImage())
					listPictures.addItem(canvas.picture);
				canvas.clear();
				setCurrentLayout(layoutLoadPicture);
				markDirty();
			}
		});
		layoutDraw.addComponent(btnSave);

		redSlider = new Slider(x, y, 158, 30, 50);
		redSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setRed(percentage);
			}
		});
		layoutDraw.addComponent(redSlider);

		greenSlider = new Slider(x, y, 158, 46, 50);
		greenSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setGreen(percentage);
			}
		});
		layoutDraw.addComponent(greenSlider);

		blueSlider = new Slider(x, y, 158, 62, 50);
		blueSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setBlue(percentage);
			}
		});
		layoutDraw.addComponent(blueSlider);

		colourDisplay = new Component(x, y, 158, 5)
		{
			@Override
			public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks)
			{
				drawRect(xPosition, yPosition, xPosition + 50, yPosition + 20, Color.DARK_GRAY.getRGB());
				drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 19, canvas.getCurrentColour());
			}
		};
		layoutDraw.addComponent(colourDisplay);

		colourGrid = new ColourGrid(x, y, 157, 82, 50, canvas, redSlider, greenSlider, blueSlider);
		layoutDraw.addComponent(colourGrid);

		displayGrid = new CheckBox("Grid", x, y, 166, 120);
		displayGrid.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				canvas.setShowGrid(displayGrid.isSelected());
			}
		});
		layoutDraw.addComponent(displayGrid);

		setCurrentLayout(layoutMainMenu);
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{
		listPictures.removeAll();

		NBTTagList pictureList = (NBTTagList) tagCompound.getTag("Pictures");
		for (int i = 0; i < pictureList.tagCount(); i++)
		{
			NBTTagCompound pictureTag = pictureList.getCompoundTagAt(i);
			Picture picture = Picture.readFromNBT(pictureTag);
			listPictures.addItem(picture);
		}
	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{
		NBTTagList pictureList = new NBTTagList();
		for (Picture picture : listPictures)
		{
			NBTTagCompound pictureTag = new NBTTagCompound();
			picture.writeToNBT(pictureTag);
			pictureList.appendTag(pictureTag);
		}
		tagCompound.setTag("Pictures", pictureList);
	}
}
