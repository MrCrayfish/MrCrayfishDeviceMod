package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.TileGrid;
import com.mrcrayfish.device.object.tiles.Tile;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationBoatRacers extends Application 
{
	private Layout layoutLevelEditor;
	private Game game;
	private TileGrid tileGrid;
	private Label labelLayer;
	private Button btnNextLayer;
	private Button btnPrevLayer;
	private CheckBox checkBoxForeground;
	private CheckBox checkBoxBackground;
	private CheckBox checkBoxPlayer;

	public ApplicationBoatRacers() 
	{
		//super("boat_racer", "Boat Racers");
		this.setDefaultWidth(320);
		this.setDefaultHeight(160);
	}
	
	@Override
	public void init() 
	{
		layoutLevelEditor = new Layout(364, 178);
		
		try 
		{
			game = new Game(4, 4, 256, 144);
			game.setEditorMode(true);
			game.setRenderPlayer(false);
			game.fill(Tile.grass);
			layoutLevelEditor.addComponent(game);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		tileGrid = new TileGrid(266, 3, game);
		layoutLevelEditor.addComponent(tileGrid);
		
		labelLayer = new Label("1", 280, 108);
		layoutLevelEditor.addComponent(labelLayer);
		
		btnNextLayer = new Button(266, 106, Icons.CHEVRON_RIGHT);
		btnNextLayer.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				game.nextLayer();
				labelLayer.setText(Integer.toString(game.getCurrentLayer().layer + 1));
			}
		});
		layoutLevelEditor.addComponent(btnNextLayer);
		
		btnPrevLayer = new Button(314, 106, Icons.CHEVRON_LEFT);
		btnPrevLayer.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				game.prevLayer();
				labelLayer.setText(Integer.toString(game.getCurrentLayer().layer + 1));
			}
		});
		layoutLevelEditor.addComponent(btnPrevLayer);
		
		checkBoxBackground = new CheckBox("Background", 3, 151);
		checkBoxBackground.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				game.setRenderBackground(checkBoxBackground.isSelected());
			}
		});
		checkBoxBackground.setSelected(true);
		layoutLevelEditor.addComponent(checkBoxBackground);
		
		checkBoxForeground = new CheckBox("Foreground", 80, 151);
		checkBoxForeground.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				game.setRenderForeground(checkBoxForeground.isSelected());
			}
		});
		checkBoxForeground.setSelected(true);
		layoutLevelEditor.addComponent(checkBoxForeground);
		
		checkBoxPlayer = new CheckBox("Player", 160, 151);
		checkBoxPlayer.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				game.setRenderPlayer(checkBoxPlayer.isSelected());
			}
		});
		layoutLevelEditor.addComponent(checkBoxPlayer);
		
		setCurrentLayout(layoutLevelEditor);
	}

	@Override
	public void load(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub

	}

	
}
