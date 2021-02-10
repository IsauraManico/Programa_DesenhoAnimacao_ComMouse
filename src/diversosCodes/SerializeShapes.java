package diversosCodes;


 import java.awt.*;
 import java.awt.event.*;
 import java.awt.font.FontRenderContext;
 import java.awt.geom.AffineTransform; 
 import java.awt.geom.*;
 import java.awt.image.BufferedImage;
 import java.io.FileNotFoundException; 
 import java.util.ArrayList; 
 import java.util.Enumeration; 
 import java.util.Random; 
 import java.util.Vector; 
 import javax.swing.*; 
 import javax.swing.border.EmptyBorder; 
 import javax.swing.event.*; 
 /** A GUI to make it easy to add/remove shapes from a canvas. It should persist the shapes between runs. */
 public class SerializeShapes 
 { 
     JPanel ui;
 }
 JPanel shapePanel; Random rand; 
 JPanel shapeCanvas; 
 DefaultListModel allShapesModel; 
 ListSelectionModel shapeSelectionModel;
 RenderingHints renderingHints; 
 SerializeShapes() { initUI(); } 
 public void initUI()
 { 
 if (ui != null)
 { return;
 } 
 } 
 renderingHints = new RenderingHints(RenderingHints.KEY_DITHERING,
 RenderingHints.VALUE_DITHER_ENABLE); 
 renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
 renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, 
 RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
 renderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
 RenderingHints.VALUE_COLOR_RENDER_QUALITY);
 
 renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
 renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
 ui = new JPanel(new BorderLayout(4, 4)); ui.setBorder(new EmptyBorder(4, 4, 4, 4));
 JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4)); 
 ui.add(controls, BorderLayout.PAGE_START); shapeCanvas = new ShapeCanvas();
 ui.add(shapeCanvas); rand = new Random(); allShapesModel = new DefaultListModel(); 
 JList allShapes = new JList(allShapesModel);
 allShapes.setCellRenderer(new ShapeListCellRenderer()); 
 shapeSelectionModel = allShapes.getSelectionModel();
 shapeSelectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION); 
 ListSelectionListener shapesSelectionListener = new ListSelectionListener() 
 { @Override
 public void valueChanged(ListSelectionEvent e) 
 { shapeCanvas.repaint();
 } };
 allShapes.addListSelectionListener(shapesSelectionListener); 
 JScrollPane shapesScroll = new JScrollPane( allShapes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
// TODO fix this hack.. 
 shapesScroll.getViewport().setPreferredSize(new Dimension(60, 200)); 
 ui.add(shapesScroll, BorderLayout.LINE_START); Action addEllipse = new AbstractAction("Ellipse")
 { @Override public void actionPerformed(ActionEvent e) 
 { int w = rand.nextInt(100) + 10; int h = rand.nextInt(100) + 10; int x = rand.nextInt(shapeCanvas.getWidth() - w); int y = rand.nextInt(shapeCanvas.getHeight() - h); Ellipse2D ellipse = new Ellipse2D.Double(x, y, w, h); addShape(ellipse); } }; addEllipse.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E); Action addRectangle = new AbstractAction("Rectangle") { @Override public void actionPerformed(ActionEvent e) { int w = rand.nextInt(100) + 10; int h = rand.nextInt(100) + 10; int x = rand.nextInt(shapeCanvas.getWidth() - w); int y = rand.nextInt(shapeCanvas.getHeight() - h); Rectangle2D rectangle = new Rectangle2D.Double(x, y, w, h); addShape(rectangle); } }; addRectangle.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R); final int faceStart = 128513; final int faceEnd = 128528; final int diff = faceEnd - faceStart; StringBuilder sb = new StringBuilder(); for (int count = faceStart; count <= faceEnd; count++) { sb.append(Character.toChars(count)); } final String s = sb.toString(); Vector compatibleFontList = new Vector(); GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); Font[] fonts = ge.getAllFonts(); for (Font font : fonts) { if (font.canDisplayUpTo(s) < 0) { compatibleFontList.add(font); } } JComboBox fontChooser = new JComboBox(compatibleFontList); ListCellRenderer fontRenderer = new DefaultListCellRenderer() { @Override public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus); JLabel l = (JLabel) c; Font font = (Font) value; l.setText(font.getName()); return l; } }; fontChooser.setRenderer(fontRenderer); final ComboBoxModel fontModel = fontChooser.getModel(); BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); Graphics2D g = bi.createGraphics(); final FontRenderContext fontRenderContext = g.getFontRenderContext(); Action addFace = new AbstractAction("Face") { @Override public void actionPerformed(ActionEvent e) { int codepoint = faceStart + rand.nextInt(diff); String text = new String(Character.toChars(codepoint)); Font font = (Font) fontModel.getSelectedItem(); Area area = new Area( font.deriveFont(80f). createGlyphVector(fontRenderContext, text). getOutline()); Rectangle bounds = area.getBounds(); float x = rand.nextInt( shapeCanvas.getWidth() - bounds.width) - bounds.x; float y = rand.nextInt( shapeCanvas.getHeight() - bounds.height) - bounds.y; AffineTransform move = AffineTransform. getTranslateInstance(x, y); area.transform(move); addShape(area); } }; addFace.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F); Action delete = new AbstractAction("Delete") { @Override public void actionPerformed(ActionEvent e) { int idx = shapeSelectionModel.getMinSelectionIndex(); if (idx < 0) { JOptionPane.showMessageDialog( ui, "Select a shape to delete", "Select a Shape", JOptionPane.ERROR_MESSAGE); } else { allShapesModel.removeElementAt(idx); shapeCanvas.repaint(); } } }; delete.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D); controls.add(new JButton(addEllipse)); controls.add(new JButton(addRectangle)); controls.add(new JButton(addFace)); controls.add(fontChooser); controls.add(new JButton(delete)); try { ArrayList shapes = deserializeShapes(); for (Shape shape : shapes) { allShapesModel.addElement(shape); } } catch (Exception ex) { System.err.println("If first launch, this is as expected!"); ex.printStackTrace(); } } private void addShape(Shape shape) { allShapesModel.addElement(shape); int size = allShapesModel.getSize() - 1; shapeSelectionModel.addSelectionInterval(size, size); } class ShapeCanvas extends JPanel { ShapeCanvas() { setBackground(Color.WHITE); } @Override public void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHints(renderingHints); Stroke stroke = new BasicStroke(1.5f); g2.setStroke(stroke); int idx = shapeSelectionModel.getMinSelectionIndex(); Shape selectedShape = null; if (idx > -1) { selectedShape = allShapesModel.get(idx); } Enumeration en = allShapesModel.elements(); while (en.hasMoreElements()) { Shape shape = (Shape) en.nextElement(); if (shape.equals(selectedShape)) { g2.setColor(new Color(0, 255, 0, 191)); } else { g2.setColor(new Color(255, 0, 0, 191)); } g2.fill(shape); g2.setColor(new Color(0, 0, 0, 224)); g2.draw(shape); } } @Override public Dimension getPreferredSize() { return new Dimension(500, 300); } } public JComponent getUI() { return ui; } public static void main(String[] args) { Runnable r = new Runnable() { @Override public void run() { SerializeShapes se = new SerializeShapes(); JFrame f = new JFrame("Serialize Shapes"); f.addWindowListener(new SerializeWindowListener(se)); f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); f.setContentPane(se.getUI()); f.setResizable(false); f.pack(); f.setLocationByPlatform(true); f.setVisible(true); } }; SwingUtilities.invokeLater(r); } public void serializeShapes() throws FileNotFoundException { ArrayList shapes = new ArrayList(); Enumeration en = allShapesModel.elements(); while (en.hasMoreElements()) { Shape shape = (Shape) en.nextElement(); shapes.add(shape); } ShapeIO.serializeShapes(shapes, this.getClass()); try { Desktop.getDesktop().open( ShapeIO.getSerializeFile(this.getClass())); } catch (Exception e) { e.printStackTrace(); } } public ArrayList deserializeShapes() throws FileNotFoundException { return ShapeIO.deserializeShapes(this.getClass()); } class ShapeListCellRenderer extends DefaultListCellRenderer { @Override public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); JLabel l = (JLabel) c; Shape shape = (Shape) value; ShapeIcon icon = new ShapeIcon(shape, 40); l.setIcon(icon); l.setText(""); return l; } } class ShapeIcon implements Icon { Shape shape; int size; ShapeIcon(Shape shape, int size) { this.shape = shape; this.size = size; } @Override public void paintIcon(Component c, Graphics g, int x, int y) { Graphics2D g2 = (Graphics2D) g; g2.setRenderingHints(renderingHints); Rectangle bounds = shape.getBounds(); int xOff = -bounds.x; int yOff = -bounds.y; double xRatio = (double) bounds.width / (double) size; double yRatio = (double) bounds.height / (double) size; double ratio = xRatio > yRatio ? xRatio : yRatio; AffineTransform scale = AffineTransform.getScaleInstance(1 / ratio, 1 / ratio); AffineTransform shift = AffineTransform.getTranslateInstance(xOff, yOff); AffineTransform totalTransform = new AffineTransform(); totalTransform.concatenate(scale); totalTransform.concatenate(shift); Area b = new Area(shape).createTransformedArea(totalTransform); bounds = b.getBounds(); g2.setColor(Color.BLACK); g2.fill(b); } @Override public int getIconWidth() { return size; } @Override public int getIconHeight() { return size; } } } class SerializeWindowListener extends WindowAdapter { SerializeShapes serializeShapes; SerializeWindowListener(SerializeShapes serializeShapes) { this.serializeShapes = serializeShapes; } @Override public void windowClosing(WindowEvent e) { try { serializeShapes.serializeShapes(); } catch (FileNotFoundException ex) { ex.printStackTrace(); System.exit(1); } System.exit(0); } } 