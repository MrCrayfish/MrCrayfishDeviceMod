package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.listener.SlideListener;
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
		//super("example", "UI Components");
		this.setDefaultWidth(270);
		this.setDefaultHeight(140);
	}
	
	@Override
	public void init() 
	{
		label = new Label("Label", 5, 5);
		super.addComponent(label);
		
		button = new Button(5, 18, "Button");
		button.setSize(63, 20);
		super.addComponent(button);
		
		leftButton = new ButtonArrow(5, 43, ButtonArrow.Type.LEFT);
		super.addComponent(leftButton);
		
		upButton = new ButtonArrow(22, 43, ButtonArrow.Type.UP);
		super.addComponent(upButton);
		
		rightButton = new ButtonArrow(39, 43, ButtonArrow.Type.RIGHT);
		super.addComponent(rightButton);
		
		downButton = new ButtonArrow(56, 43, ButtonArrow.Type.DOWN);
		super.addComponent(downButton);
		
		itemList = new ItemList<String>(5, 60, 63, 4);
		itemList.addItem("Item #1");
		itemList.addItem("Item #2");
		itemList.addItem("Item #3");
		super.addComponent(itemList);
		
		checkBoxOff = new CheckBox("Off", 5, 122);
		super.addComponent(checkBoxOff);
		
		checkBoxOn = new CheckBox("On", 42, 122);
		checkBoxOn.setSelected(true);
		super.addComponent(checkBoxOn);
		
		textField = new TextField(88, 5, 80);
		textField.setText("Text Field");
		super.addComponent(textField);
		
		textArea = new TextArea(88, 25, 80, 60);
		textArea.setText("Text Area");
		super.addComponent(textArea);
		
		progressBar = new ProgressBar(88, 90, 80, 16);
		progressBar.setProgress(75);
		super.addComponent(progressBar);
		
		slider = new Slider(88, 111, 80);
		slider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				progressBar.setProgress((int) (100 * percentage));
			}
		});
		super.addComponent(slider);
		
		spinner = new Spinner(56, 3);
		super.addComponent(spinner);
		
		text = new Text("", 180, 5, 90);
		text.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
		super.addComponent(text);
		
		image = new Image(180, 100, 85, 35, "https://minecraft.net/static/pages/img/minecraft-hero-og.c5517b7973e1.jpg");
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
