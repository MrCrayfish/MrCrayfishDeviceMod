package com.mrcrayfish.device.api.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Wrappable;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.print.task.TaskPrint;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import com.mrcrayfish.device.programs.system.object.ColourScheme;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public abstract class Dialog extends Wrappable {
	private String title = "Message";
	private int width;
	private int height;

	protected final Layout defaultLayout;
	private Layout customLayout;

	private boolean pendingLayoutUpdate = true;
	private boolean pendingClose = false;

	public Dialog() {
		defaultLayout = new Layout(150, 40);
	}

	protected final void addComponent(Component c) {
		if (c != null) {
			defaultLayout.addComponent(c);
			c.init(defaultLayout);
		}
	}

	protected final void setLayout(Layout layout) {
		customLayout = layout;
		width = layout.width;
		height = layout.height;
		pendingLayoutUpdate = true;
		customLayout.init();
	}

	@Override
	public void init() {
		defaultLayout.clear();
		setLayout(defaultLayout);
	}

	@Override
	public void onTick() {
		if (pendingClose && (getWindow().getDialogWindow() == null)) {
			getWindow().close();
		}
		customLayout.handleTick();
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active,
			float partialTicks) {
		customLayout.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
		customLayout.renderOverlay(laptop, mc, mouseX, mouseY, active);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		customLayout.handleMouseClick(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
		customLayout.handleMouseDrag(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
		customLayout.handleMouseRelease(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
		customLayout.handleMouseScroll(mouseX, mouseY, direction);
	}

	@Override
	public void handleKeyTyped(char character, int code) {
		customLayout.handleKeyTyped(character, code);
	}

	@Override
	public void handleKeyReleased(char character, int code) {
		customLayout.handleKeyReleased(character, code);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getWindowTitle() {
		return title;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void markForLayoutUpdate() {
		pendingLayoutUpdate = true;
	}

	@Override
	public boolean isPendingLayoutUpdate() {
		return pendingLayoutUpdate;
	}

	@Override
	public void clearPendingLayout() {
		pendingLayoutUpdate = false;
	}

	@Override
	public void updateComponents(int x, int y) {
		customLayout.updateComponents(x, y);
	}

	@Override
	public void onClose() {
	}

	public void close() {
		pendingClose = true;
	}

	public static class Message extends Dialog {
		private String messageText = "";

		private ClickListener positiveListener;
		private Button buttonPositive;

		public Message(String messageText) {
			this.messageText = messageText;
		}

		@Override
		public void init() {
			super.init();

			int lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(messageText, getWidth() - 10)
					.size();
			defaultLayout.height += (lines - 1) * 9;

			super.init();

			defaultLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> Gui.drawRect(x,
					y, x + width, y + height, Color.LIGHT_GRAY.getRGB()));

			Text message = new Text(messageText, 5, 5, getWidth() - 10);
			addComponent(message);

			buttonPositive = new Button(getWidth() - 41, getHeight() - 20, "Close");
			buttonPositive.setSize(36, 15);
			buttonPositive.setClickListener((c, mouseButton) -> {
				if (positiveListener != null) {
					positiveListener.onClick(c, mouseButton);
				}
				close();
			});
			addComponent(buttonPositive);
		}
	}

	/**
	 * A simple confirmation dialog
	 *
	 * This can be used to prompt as user to confirm whether a task should run. For
	 * instance, the FileBrowser component uses this dialog to prompt the user if it
	 * should override a file.
	 */
	public static class Confirmation extends Dialog {
		private String messageText = "Are you sure?";
		private String positiveText = "Yes";
		private String negativeText = "No";

		private ClickListener positiveListener;
		private ClickListener negativeListener;

		private Button buttonPositive;
		private Button buttonNegative;

		public Confirmation() {
		}

		public Confirmation(String messageText) {
			this.messageText = messageText;
		}

		@Override
		public void init() {
			super.init();

			int lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(messageText, getWidth() - 10)
					.size();
			defaultLayout.height += (lines - 1) * 9;

			super.init();

			defaultLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> Gui.drawRect(x,
					y, x + width, y + height, Color.LIGHT_GRAY.getRGB()));

			Text message = new Text(messageText, 5, 5, getWidth() - 10);
			addComponent(message);

			buttonPositive = new Button(getWidth() - 35, getHeight() - 20, positiveText);
			buttonPositive.setSize(30, 15);
			buttonPositive.setClickListener((c, mouseButton) -> {
				if (positiveListener != null) {
					positiveListener.onClick(c, mouseButton);
				}
				close();
			});
			addComponent(buttonPositive);

			buttonNegative = new Button(getWidth() - 70, getHeight() - 20, negativeText);
			buttonPositive.setSize(30, 15);
			buttonNegative.setClickListener((c, mouseButton) -> {
				if (negativeListener != null) {
					negativeListener.onClick(c, mouseButton);
				}
				close();
			});
			addComponent(buttonNegative);
		}

		/**
		 * Sets the positive button text
		 * 
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText) {
			if (positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText) {
			if (negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		public void setPositiveListener(ClickListener positiveListener) {
			this.positiveListener = positiveListener;
		}

		public void setNegativeListener(ClickListener negativeListener) {
			this.negativeListener = negativeListener;
		}

		public void setMessageText(String messageText) {
			this.messageText = messageText;
		}
	}

	/**
	 * A simple dialog to retrieve text input from the user
	 */
	public static class Input extends Dialog {
		private String messageText = null;
		private String inputText = "";
		private String positiveText = "Okay";
		private String negativeText = "Cancel";

		private ResponseHandler<String> responseListener;

		private TextField textFieldInput;
		private Button buttonPositive;
		private Button buttonNegative;

		public Input() {
		}

		public Input(String messageText) {
			this.messageText = messageText;
		}

		@Override
		public void init() {
			super.init();

			int offset = 0;

			if (messageText != null) {
				int lines = Minecraft.getMinecraft().fontRenderer
						.listFormattedStringToWidth(messageText, getWidth() - 10).size();
				defaultLayout.height += (lines * 9) + 10;
				offset += (lines * 9) + 5;
			}

			super.init();

			defaultLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
				Gui.drawRect(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
			});

			if (messageText != null) {
				Text message = new Text(messageText, 5, 5, getWidth() - 10);
				addComponent(message);
			}

			textFieldInput = new TextField(5, 5 + offset, getWidth() - 10);
			textFieldInput.setText(inputText);
			textFieldInput.setFocused(true);
			addComponent(textFieldInput);

			int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
			buttonPositive = new Button(getWidth() - positiveWidth - 15, getHeight() - 20, positiveText);
			buttonPositive.setSize(positiveWidth + 10, 15);
			buttonPositive.setClickListener((c, mouseButton) -> {
				if (!textFieldInput.getText().isEmpty()) {
					boolean close = true;
					if (responseListener != null) {
						close = responseListener.onResponse(true, textFieldInput.getText().trim());
					}
					if (close) {
						close();
					}
				}
			});
			addComponent(buttonPositive);

			int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
			buttonNegative = new Button(getWidth() - positiveWidth - negativeWidth - 15 - 15, getHeight() - 20,
					negativeText);
			buttonNegative.setSize(negativeWidth + 10, 15);
			buttonNegative.setClickListener((c, mouseButton) -> close());
			addComponent(buttonNegative);
		}

		/**
		 * Sets the initial text for the input text field
		 * 
		 * @param inputText
		 */
		public void setInputText(@Nonnull String inputText) {
			if (inputText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.inputText = inputText;
		}

		/**
		 * Gets the input text field. This will be null if has not been
		 * 
		 * @return
		 */
		@Nullable
		public TextField getTextFieldInput() {
			return textFieldInput;
		}

		/**
		 * Sets the positive button text
		 * 
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText) {
			if (positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText) {
			if (negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		/**
		 * Sets the response handler. The handler is called when the positive button is
		 * pressed and returns the value in the input text field. Returning true in the
		 * handler indicates that the dialog should close.
		 *
		 * @param responseListener
		 */
		public void setResponseHandler(ResponseHandler<String> responseListener) {
			this.responseListener = responseListener;
		}
	}

	public static class OpenFile extends Dialog {
		private final Application app;

		private String positiveText = "Open";
		private String negativeText = "Cancel";

		private Layout main;
		private FileBrowser browser;
		private Button buttonPositive;
		private Button buttonNegative;

		private ResponseHandler<File> responseListener;
		private Predicate<File> filter;

		public OpenFile(Application app) {
			this.app = app;
			setTitle("Open File");
		}

		@Override
		public void init() {
			super.init();

			main = new Layout(210, 124);

			browser = new FileBrowser(0, 0, app, FileBrowser.Mode.BASIC);
			browser.openFolder(FileSystem.DIR_HOME);
			browser.setFilter(file -> (filter == null) || filter.test(file) || file.isFolder());
			browser.setItemClickListener((file, index, mouseButton) -> {
				if (mouseButton == 0) {
					if (!file.isFolder()) {
						buttonPositive.setEnabled(true);
					}
				}
			});
			main.addComponent(browser);

			int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
			buttonPositive = new Button(172, 105, positiveText);
			buttonPositive.setSize(positiveWidth + 10, 15);
			buttonPositive.setEnabled(false);
			buttonPositive.setClickListener((c, mouseButton) -> {
				if (mouseButton == 0) {
					File file = browser.getSelectedFile();
					if (file != null) {
						boolean close = true;
						if (responseListener != null) {
							close = responseListener.onResponse(true, file);
						}
						if (close) {
							close();
						}
					}
				}
			});
			main.addComponent(buttonPositive);

			int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
			buttonNegative = new Button(125, 105, negativeText);
			buttonNegative.setSize(negativeWidth + 10, 15);
			buttonNegative.setClickListener((c, mouseButton) -> close());
			main.addComponent(buttonNegative);

			setLayout(main);
		}

		/**
		 * Sets the positive button text
		 * 
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText) {
			if (positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText) {
			if (negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		/**
		 * Sets the response handler. The handler is called when the positive button is
		 * pressed and returns the file that is selected. Returning true in the handler
		 * indicates that the dialog should close.
		 *
		 * @param responseListener
		 */
		public void setResponseHandler(ResponseHandler<File> responseListener) {
			this.responseListener = responseListener;
		}

		public void setFilter(Predicate<File> filter) {
			this.filter = filter;
		}
	}

	public static class SaveFile extends Dialog {
		private final Application app;
		private String name;
		private NBTTagCompound data;

		private String positiveText = "Save";
		private String negativeText = "Cancel";

		private Layout main;
		private FileBrowser browser;
		private TextField textFieldFileName;
		private Button buttonPositive;
		private Button buttonNegative;

		public ResponseHandler<File> responseHandler;
		private Predicate<File> filter;

		private String path = FileSystem.DIR_HOME;

		public SaveFile(Application app, NBTTagCompound data) {
			this.app = app;
			this.data = data;
			setTitle("Save File");
		}

		public SaveFile(Application app, File file) {
			this.app = app;
			name = file.getName();
			data = file.toTag();
			setTitle("Save File");
		}

		@Override
		public void init() {
			super.init();
			main = new Layout(210, 142);

			browser = new FileBrowser(0, 0, app, FileBrowser.Mode.BASIC);
			browser.setFilter(file -> (filter == null) || filter.test(file) || file.isFolder());
			browser.openFolder(path);
			main.addComponent(browser);

			Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
			buttonPositive = new Button(172, 123, positiveText);
			buttonPositive.setClickListener((c, mouseButton) -> {
				if (mouseButton == 0) {
					if (!textFieldFileName.getText().isEmpty()) {
						if (!FileSystem.PATTERN_FILE_NAME.matcher(textFieldFileName.getText()).matches()) {
							Dialog.Message dialog = new Dialog.Message(
									"File name may only contain letters, numbers, underscores and spaces.");
							app.openDialog(dialog);
							return;
						}

						File file;
						if (name != null) {
							file = File.fromTag(textFieldFileName.getText(), data);
						} else {
							file = new File(textFieldFileName.getText(), app, data.copy());
						}

						browser.addFile(file, (response, success) -> {
							if (response.getStatus() == FileSystem.Status.FILE_EXISTS) {
								Dialog.Confirmation dialog = new Dialog.Confirmation(
										"A file with that name already exists. Are you sure you want to override it?");
								dialog.setPositiveText("Override");
								dialog.setPositiveListener((c1, mouseButton1) -> {
									browser.removeFile(file.getName());
									browser.addFile(file);
									dialog.close();

									// TODO Look into better handling. Get response from parent if should close.
									// Maybe a response interface w/ generic
									if (SaveFile.this.responseHandler != null) {
										SaveFile.this.responseHandler.onResponse(true, file);
									}
									SaveFile.this.close();
								});
								app.openDialog(dialog);
							} else {
								if (responseHandler != null) {
									responseHandler.onResponse(true, file);
								}
								close();
							}
						});
					}
				}
			});
			main.addComponent(buttonPositive);

			Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
			buttonNegative = new Button(126, 123, negativeText);
			buttonNegative.setClickListener((c, mouseButton) -> close());
			main.addComponent(buttonNegative);

			textFieldFileName = new TextField(26, 105, 180);
			textFieldFileName.setFocused(true);
			if (name != null) {
				textFieldFileName.setText(name);
			}
			main.addComponent(textFieldFileName);

			setLayout(main);
		}

		/**
		 * Sets the positive button text
		 * 
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText) {
			if (positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText) {
			if (negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		/**
		 * Sets the response handler. The handler is called when the positive button is
		 * pressed and returns the file that is selected. Returning true in the handler
		 * indicates that the dialog should close.
		 *
		 * @param responseHandler
		 */
		public void setResponseHandler(ResponseHandler<File> responseHandler) {
			this.responseHandler = responseHandler;
		}

		public void setFilter(Predicate<File> filter) {
			this.filter = filter;
		}

		public void setFolder(String path) {
			this.path = path;
		}
	}

	public static class Print extends Dialog {
		private final IPrint print;

		private Layout layoutMain;
		private Label labelMessage;
		private Button buttonRefresh;
		private ItemList<PrinterEntry> itemListPrinters;
		private Button buttonPrint;
		private Button buttonCancel;
		private Button buttonInfo;

		public Print(IPrint print) {
			this.print = print;
			setTitle("Print");
		}

		@Override
		public void init() {
			super.init();

			layoutMain = new Layout(150, 132);

			labelMessage = new Label("Select a Printer", 5, 5);
			layoutMain.addComponent(labelMessage);

			buttonRefresh = new Button(131, 2, Icons.RELOAD);
			buttonRefresh.setPadding(2);
			buttonRefresh.setToolTip("Refresh", "Retrieve an updated list of printers");
			buttonRefresh.setClickListener((c, mouseButton) -> {
				if (mouseButton == 0) {
					itemListPrinters.setSelectedIndex(-1);
					itemListPrinters.setItems(getPrinters());
				}
			});
			layoutMain.addComponent(buttonRefresh);

			itemListPrinters = new ItemList<>(5, 18, 140, 5);
			itemListPrinters.setListItemRenderer(new ListItemRenderer<PrinterEntry>(16) {
				@Override
				public void render(PrinterEntry printerEntry, Gui gui, Minecraft mc, int x, int y, int width,
						int height, boolean selected) {
					ColourScheme colourScheme = Laptop.getSystem().getSettings().getColourScheme();
					Gui.drawRect(x, y, x + width, y + height,
							selected ? colourScheme.getItemHighlightColour() : colourScheme.getItemBackgroundColour());
					Icons.PRINTER.draw(mc, x + 3, y + 3);
					RenderUtil.drawStringClipped(printerEntry.getName(), x + 18, y + 4, 118,
							Laptop.getSystem().getSettings().getColourScheme().getTextColour(), true);
				}
			});
			itemListPrinters.setItemClickListener((blockPos, index, mouseButton) -> {
				if (mouseButton == 0) {
					buttonPrint.setEnabled(true);
					buttonInfo.setEnabled(true);
				}
			});
			itemListPrinters.sortBy((o1, o2) -> {
				BlockPos laptopPos = Laptop.getPos();

				BlockPos pos1 = o1.getPos();
				double distance1 = laptopPos.distanceSqToCenter(pos1.getX() + 0.5, pos1.getY() + 0.5,
						pos1.getZ() + 0.5);

				BlockPos pos2 = o2.getPos();
				double distance2 = laptopPos.distanceSqToCenter(pos2.getX() + 0.5, pos2.getY() + 0.5,
						pos2.getZ() + 0.5);

				return distance2 < distance1 ? 1 : (distance1 == distance2) ? 0 : -1;
			});
			itemListPrinters.setItems(getPrinters());
			layoutMain.addComponent(itemListPrinters);

			buttonPrint = new Button(98, 108, "Print", Icons.CHECK);
			buttonPrint.setPadding(5);
			buttonPrint.setEnabled(false);
			buttonPrint.setClickListener((c, mouseButton) -> {
				if (mouseButton == 0) {
					PrinterEntry printerEntry = itemListPrinters.getSelectedItem();
					if (printerEntry != null) {
						TaskPrint task = new TaskPrint(printerEntry.getPos(), print);
						task.setCallback((nbtTagCompound, success) -> {
							if (success) {
								close();
							}
						});
						TaskManager.sendTask(task);
					}
				}
			});
			layoutMain.addComponent(buttonPrint);

			buttonCancel = new Button(74, 108, Icons.CROSS);
			buttonCancel.setPadding(5);
			buttonCancel.setClickListener((c, mouseButton) -> {
				if (mouseButton == 0) {
					close();
				}
			});
			layoutMain.addComponent(buttonCancel);

			buttonInfo = new Button(5, 108, Icons.HELP);
			buttonInfo.setEnabled(false);
			buttonInfo.setPadding(5);
			buttonInfo.setClickListener((c, mouseButton) -> {
				if (mouseButton == 0) {
					PrinterEntry printerEntry = itemListPrinters.getSelectedItem();
					if (printerEntry != null) {
						Info info = new Info(printerEntry);
						openDialog(info);
					}
				}
			});
			layoutMain.addComponent(buttonInfo);

			setLayout(layoutMain);
		}

		private List<PrinterEntry> getPrinters() {
			List<PrinterEntry> printers = new ArrayList<>();

			World world = Minecraft.getMinecraft().world;
			BlockPos laptopPos = Laptop.getPos();
			int range = 20;

			for (int y = -range; y < (range + 1); y++) {
				for (int z = -range; z < (range + 1); z++) {
					for (int x = -range; x < (range + 1); x++) {
						BlockPos pos = new BlockPos(laptopPos.getX() + x, laptopPos.getY() + y, laptopPos.getZ() + z);
						IBlockState state = world.getBlockState(pos);
						if (state.getBlock() == DeviceBlocks.PRINTER) {
							TileEntity tileEntity = world.getTileEntity(pos);
							if (tileEntity instanceof TileEntityPrinter) {
								TileEntityPrinter tileEntityPrinter = (TileEntityPrinter) tileEntity;
								printers.add(new PrinterEntry(tileEntityPrinter.getName(),
										tileEntityPrinter.getPaperCount(), pos));
							}
						}
					}
				}
			}
			return printers;
		}

		private static class PrinterEntry {
			private String name;
			private int paperCount;
			private BlockPos pos;

			public PrinterEntry(String name, int paperCount, BlockPos pos) {
				this.name = name;
				this.paperCount = paperCount;
				this.pos = pos;
			}

			public String getName() {
				return name;
			}

			public int getPaperCount() {
				return paperCount;
			}

			public BlockPos getPos() {
				return pos;
			}
		}

		private static class Info extends Dialog {
			private final PrinterEntry entry;

			private Layout layoutMain;
			private Label labelName;
			private Label labelPaper;
			private Label labelPosition;
			private Button buttonClose;

			private Info(PrinterEntry entry) {
				this.entry = entry;
				setTitle("Details");
			}

			@Override
			public void init() {
				super.init();

				layoutMain = new Layout(120, 70);

				labelName = new Label(TextFormatting.GOLD.toString() + TextFormatting.BOLD.toString() + entry.getName(),
						5, 5);
				layoutMain.addComponent(labelName);

				labelPaper = new Label(TextFormatting.DARK_GRAY + "Paper: " + TextFormatting.RESET
						+ Integer.toString(entry.getPaperCount()), 5, 18);
				labelPaper.setAlignment(Component.ALIGN_LEFT);
				labelPaper.setShadow(false);
				layoutMain.addComponent(labelPaper);

				String position = TextFormatting.DARK_GRAY + "X: " + TextFormatting.RESET + entry.getPos().getX() + " "
						+ TextFormatting.DARK_GRAY + "Y: " + TextFormatting.RESET + entry.getPos().getY() + " "
						+ TextFormatting.DARK_GRAY + "Z: " + TextFormatting.RESET + entry.getPos().getZ();
				labelPosition = new Label(position, 5, 30);
				labelPosition.setShadow(false);
				layoutMain.addComponent(labelPosition);

				buttonClose = new Button(5, 49, "Close");
				buttonClose.setClickListener((c, mouseButton) -> {
					if (mouseButton == 0) {
						close();
					}
				});
				layoutMain.addComponent(buttonClose);

				setLayout(layoutMain);
			}
		}
	}

	/**
	 * The response listener interface. Used for handling responses from components.
	 * The generic is the returned value.
	 *
	 * @author MrCrayfish
	 */
	public interface ResponseHandler<E> {
		/**
		 * Called when a response is thrown.
		 *
		 * @param success
		 *            if the executing task was successful
		 */
		boolean onResponse(boolean success, E e);
	}

}
