package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ButtonArrow;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.ProgressBar;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.api.app.component.Spinner;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.SlideListener;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationExample extends Application
{
	private Label label;
	private Button button;
	private ButtonArrow leftButton;
	private ButtonArrow upButton;
	private ButtonArrow rightButton;
	private ButtonArrow downButton;
	private ItemList<String> itemList;
	private CheckBox checkBoxOn;
	private CheckBox checkBoxOff;
	private ProgressBar progressBar;
	private Slider slider;
	private Spinner spinner;
	private TextField textField;
	private TextArea textArea;
	private Text text;
	private Image image;
	
	public ApplicationExample() 
	{
		super("example", "UI Components");
		this.setDefaultWidth(270);
		this.setDefaultHeight(140);
	}
	
	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		
		label = new Label("Label", x, y, 5, 5);
		super.addComponent(label);
		
		button = new Button("Button", x, y, 5, 18, 64, 20);
		super.addComponent(button);
		
		leftButton = new ButtonArrow(x, y, 5, 43, ButtonArrow.Type.LEFT);
		super.addComponent(leftButton);
		
		upButton = new ButtonArrow(x, y, 22, 43, ButtonArrow.Type.UP);
		super.addComponent(upButton);
		
		rightButton = new ButtonArrow(x, y, 39, 43, ButtonArrow.Type.RIGHT);
		super.addComponent(rightButton);
		
		downButton = new ButtonArrow(x, y, 56, 43, ButtonArrow.Type.DOWN);
		super.addComponent(downButton);
		
		itemList = new ItemList<String>(x, y, 5, 60, 63, 4);
		itemList.addItem("Item #1");
		itemList.addItem("Item #2");
		itemList.addItem("Item #3");
		super.addComponent(itemList);
		
		checkBoxOff = new CheckBox("Off", x, y, 5, 122);
		super.addComponent(checkBoxOff);
		
		checkBoxOn = new CheckBox("On", x, y, 42, 122);
		checkBoxOn.setSelected(true);
		super.addComponent(checkBoxOn);
		
		textField = new TextField(x, y, 88, 5, 80);
		textField.setText("Text Field");
		super.addComponent(textField);
		
		textArea = new TextArea(x, y, 88, 25, 80, 60);
		textArea.setText("Text Area");
		super.addComponent(textArea);
		
		progressBar = new ProgressBar(x, y, 88, 90, 80, 16);
		progressBar.setProgress(75);
		super.addComponent(progressBar);
		
		slider = new Slider(x, y, 88, 111, 80);
		slider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				progressBar.setProgress((int) (100 * percentage));
			}
		});
		super.addComponent(slider);
		
		spinner = new Spinner(x, y, 57, 3);
		super.addComponent(spinner);
		
		text = new Text("", x, y, 180, 5, 90);
		text.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
		super.addComponent(text);
		
		image = new Image(x, y, 180, 100, 85, 50, 0, 0, 256, 211, Laptop.WALLPAPERS.get(0));
		image.setAlpha(0.8F);
		super.addComponent(image);
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
