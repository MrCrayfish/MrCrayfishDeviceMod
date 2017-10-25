package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.app.listener.SlideListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.object.Canvas;
import com.mrcrayfish.device.object.ColourGrid;
import com.mrcrayfish.device.object.Picture;
import com.mrcrayfish.device.object.Picture.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ApplicationPixelPainter extends Application
{
	private static final ResourceLocation PIXEL_PAINTER_ICONS = new ResourceLocation("cdm:textures/gui/pixel_painter.png");

	private static final Color ITEM_BACKGROUND = new Color(170, 176, 194);
	private static final Color ITEM_SELECTED = new Color(200, 176, 174);
	private static final Color AUTHOR_TEXT = new Color(114, 120, 138);

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
	private ItemList<Picture> listPictures;
	private Button btnLoadSavedPicture;
	private Button btnBrowseSavedPicture;
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
		//super("pixel_painter", "Pixel Painter");
	}

	@Override
	public void init()
	{
		/* Main Menu */
		layoutMainMenu = new Layout(100, 100);

		logo = new Image(35, 5, 28, 28, info.getIconU(), info.getIconV(), 14, 14, Laptop.ICON_TEXTURES);
		layoutMainMenu.addComponent(logo);

		labelLogo = new Label("Pixel Painter", 19, 35);
		layoutMainMenu.addComponent(labelLogo);

		btnNewPicture = new Button(5, 50, "New");
		btnNewPicture.setSize(90, 20);
		btnNewPicture.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutNewPicture);
			}
		});
		layoutMainMenu.addComponent(btnNewPicture);

		btnLoadPicture = new Button(5, 75, "Load");
		btnLoadPicture.setSize(90, 20);
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

		labelName = new Label("Name", 5, 5);
		layoutNewPicture.addComponent(labelName);

		fieldName = new TextField(5, 15, 100);
		layoutNewPicture.addComponent(fieldName);

		labelAuthor = new Label("Author", 5, 35);
		layoutNewPicture.addComponent(labelAuthor);

		fieldAuthor = new TextField(5, 45, 100);
		layoutNewPicture.addComponent(fieldAuthor);

		labelSize = new Label("Size", 110, 5);
		layoutNewPicture.addComponent(labelSize);

		RadioGroup sizeGroup = new RadioGroup();

		checkBox16x = new CheckBox("16x", 110, 17);
		checkBox16x.setSelected(true);
		checkBox16x.setRadioGroup(sizeGroup);
		layoutNewPicture.addComponent(checkBox16x);

		checkBox32x = new CheckBox("32x", 145, 17);
		checkBox32x.setRadioGroup(sizeGroup);
		layoutNewPicture.addComponent(checkBox32x);

		btnCreatePicture = new Button(110, 40, "Create");
		btnCreatePicture.setSize(65, 20);
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
		
		layoutLoadPicture = new Layout(165, 116);
		layoutLoadPicture.setInitListener(() ->
		{
			listPictures.removeAll();
			FileSystem.getApplicationFolder(this, (folder, success) ->
			{
                if(success)
				{
					folder.search(file -> file.isForApplication(this)).forEach(file ->
					{
						Picture picture = Picture.fromFile(file);
						listPictures.addItem(picture);
					});
				}
            });
        });

		listPictures = new ItemList<>(5, 5, 100, 5);
		listPictures.setListItemRenderer(new ListItemRenderer<Picture>(20)
		{
			@Override
			public void render(Picture picture, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				Gui.drawRect(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());
				mc.fontRendererObj.drawString(picture.getName(), x + 2, y + 2, Color.WHITE.getRGB(), false);
				mc.fontRendererObj.drawString(picture.getAuthor(), x + 2, y + 11, AUTHOR_TEXT.getRGB(), false);
			}
		});
		listPictures.setItemClickListener((picture, index, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				btnLoadSavedPicture.setEnabled(true);
				btnDeleteSavedPicture.setEnabled(true);
			}
        });
		layoutLoadPicture.addComponent(listPictures);

		btnLoadSavedPicture = new Button(110, 5, "Load");
		btnLoadSavedPicture.setSize(50, 20);
		btnLoadSavedPicture.setEnabled(false);
		btnLoadSavedPicture.setClickListener((c, mouseButton) ->
		{
            if (listPictures.getSelectedIndex() != -1)
            {
                canvas.setPicture(listPictures.getSelectedItem());
                setCurrentLayout(layoutDraw);
            }
        });
		layoutLoadPicture.addComponent(btnLoadSavedPicture);

		btnBrowseSavedPicture = new Button(110, 30, "Browse");
		btnBrowseSavedPicture.setSize(50, 20);
		btnBrowseSavedPicture.setClickListener((c, mouseButton) ->
		{
			Dialog.OpenFile dialog = new Dialog.OpenFile(this);
			dialog.setResponseHandler((success, file) ->
			{
				if(file.isForApplication(this))
				{
					Picture picture = Picture.fromFile(file);
					canvas.setPicture(picture);
					setCurrentLayout(layoutDraw);
					return true;
				}
				else
				{
					Dialog.Message dialog2 = new Dialog.Message("Invalid file for Pixel Painter");
					openDialog(dialog2);
				}
				return false;
			});
			openDialog(dialog);
        });
		layoutLoadPicture.addComponent(btnBrowseSavedPicture);

		btnDeleteSavedPicture = new Button(110, 55, "Delete");
		btnDeleteSavedPicture.setSize(50, 20);
		btnDeleteSavedPicture.setEnabled(false);
		btnDeleteSavedPicture.setClickListener((c, mouseButton) ->
		{
			if(listPictures.getSelectedIndex() != -1)
			{
				Picture picture = listPictures.getSelectedItem();
				File file = picture.getSource();
				if(file != null)
				{
					file.delete((o, success) ->
					{
						if(success)
						{
							listPictures.removeItem(listPictures.getSelectedIndex());
							btnDeleteSavedPicture.setEnabled(false);
							btnLoadSavedPicture.setEnabled(false);
						}
						else
						{
							//TODO error dialog
						}
                    });
				}
				else
				{
					//TODO error dialog
				}
			}
		});
		layoutLoadPicture.addComponent(btnDeleteSavedPicture);

		btnBackSavedPicture = new Button(110, 80, "Back");
		btnBackSavedPicture.setSize(50, 20);
		btnBackSavedPicture.setClickListener((c, mouseButton) -> setCurrentLayout(layoutMainMenu));
		layoutLoadPicture.addComponent(btnBackSavedPicture);

		
		/* Drawing */
		
		layoutDraw = new Layout(213, 140);

		canvas = new Canvas(5, 5);
		layoutDraw.addComponent(canvas);

		RadioGroup toolGroup = new RadioGroup();

		btnPencil = new ButtonToggle(138, 5, PIXEL_PAINTER_ICONS, 0, 0, 10, 10);
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

		btnBucket = new ButtonToggle(138, 24, PIXEL_PAINTER_ICONS, 10, 0, 10, 10);
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

		btnEraser = new ButtonToggle(138, 43, PIXEL_PAINTER_ICONS, 20, 0, 10, 10);
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

		btnEyeDropper = new ButtonToggle(138, 62, PIXEL_PAINTER_ICONS, 30, 0, 10, 10);
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

		btnCancel = new Button(138, 100, PIXEL_PAINTER_ICONS, 50, 0, 10, 10);
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

		btnSave = new Button(138, 119, PIXEL_PAINTER_ICONS, 40, 0, 10, 10);
		btnSave.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				canvas.picture.pixels = canvas.copyPixels();

				NBTTagCompound pictureTag = new NBTTagCompound();
				canvas.picture.writeToNBT(pictureTag);

				if(canvas.isExistingImage())
				{
					File file = canvas.picture.getSource();
					if(file != null)
					{
						file.setData(pictureTag, (response, success) ->
						{
                            if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
							{
								canvas.clear();
								setCurrentLayout(layoutLoadPicture);
							}
							else
							{
								//TODO error dialog
							}
                        });
					}
				}
				else
				{
					Dialog.SaveFile dialog = new Dialog.SaveFile(ApplicationPixelPainter.this, pictureTag);
					dialog.setResponseHandler((success, file) ->
					{
						if(success)
						{
							canvas.clear();
							setCurrentLayout(layoutLoadPicture);
							return true;
						}
						else
						{
							//TODO error dialog
						}
						return false;
					});
					openDialog(dialog);
				}
			}
		});
		layoutDraw.addComponent(btnSave);

		redSlider = new Slider(158, 30, 50);
		redSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setRed(percentage);
			}
		});
		layoutDraw.addComponent(redSlider);

		greenSlider = new Slider(158, 46, 50);
		greenSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setGreen(percentage);
			}
		});
		layoutDraw.addComponent(greenSlider);

		blueSlider = new Slider(158, 62, 50);
		blueSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setBlue(percentage);
			}
		});
		layoutDraw.addComponent(blueSlider);

		colourDisplay = new Component(158, 5)
		{
			@Override
			public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
			{
				drawRect(xPosition, yPosition, xPosition + 50, yPosition + 20, Color.DARK_GRAY.getRGB());
				drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 19, canvas.getCurrentColour());
			}
		};
		layoutDraw.addComponent(colourDisplay);

		colourGrid = new ColourGrid(157, 82, 50, canvas, redSlider, greenSlider, blueSlider);
		layoutDraw.addComponent(colourGrid);

		displayGrid = new CheckBox("Grid", 166, 120);
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

	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{

	}

	@Override
	public void onClose()
	{
		super.onClose();
		listPictures.removeAll();
	}
}
