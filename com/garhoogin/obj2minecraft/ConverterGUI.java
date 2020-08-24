package com.garhoogin.obj2minecraft;

import static com.garhoogin.obj2minecraft.Main.MAX_THREADS;
import com.garhoogin.obj2minecraft.world.World;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConverterGUI {
    
    private final JFrame frame;
    
    public ConverterGUI(){
        //I seriously hate GUI programming in Java
        this.frame = new JFrame("Obj2Mc");
        this.frame.setResizable(false);
        this.frame.setLayout(null);
        
        JPanel inputPanelBorder = new JPanel();
        inputPanelBorder.setBorder(BorderFactory.createTitledBorder("Input"));
        inputPanelBorder.setBounds(10, 10, 400, 100);
        JPanel inputPanel = new JPanel();
        GroupLayout groupLayout1 = new GroupLayout(inputPanel);
        inputPanel.setLayout(groupLayout1);
        
        JPanel outputPanelBorder = new JPanel();
        outputPanelBorder.setBorder(BorderFactory.createTitledBorder("Output"));
        outputPanelBorder.setBounds(10, 120, 400, 100);
        JPanel outputPanel = new JPanel();
        GroupLayout groupLayout2 = new GroupLayout(outputPanel);
        outputPanel.setLayout(groupLayout2);
        
        
        Label inputModelLabel = new Label("Model:");
        TextField inputModelTextField = new TextField("");
        Button inputModelBrowseButton = new Button("...");
        Label inputMaterialData = new Label("Material Definitions *:");
        TextField inputMaterialDataTextField = new TextField("");
        Button inputMaterialDataBrowseButton = new Button("...");
        inputModelTextField.setPreferredSize(new Dimension(200, 22));
        inputModelLabel.setPreferredSize(new Dimension(120, 22));
        
        
        groupLayout1.setHorizontalGroup(groupLayout1.createSequentialGroup()
            .addGroup(groupLayout1.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(inputModelLabel)
                .addComponent(inputMaterialData))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(groupLayout1.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(inputModelTextField)
                .addComponent(inputMaterialDataTextField))
            .addGroup(groupLayout1.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(inputModelBrowseButton)
                .addComponent(inputMaterialDataBrowseButton)));
        groupLayout1.setVerticalGroup(groupLayout1.createSequentialGroup()
            .addGroup(groupLayout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(inputModelLabel)
                .addComponent(inputModelTextField)
                .addComponent(inputModelBrowseButton))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(groupLayout1.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(inputMaterialData)
                .addComponent(inputMaterialDataTextField)
                .addComponent(inputMaterialDataBrowseButton))
        );
        
        Label heightLabel = new Label("Height:");
        Label outDirectoryLabel = new Label("Output Directory:");
        Button outDirectoryBrowseButton = new Button("...");
        TextField heightTextField = new TextField("100");
        TextField outDirectoryTextField = new TextField("");
        outDirectoryTextField.setPreferredSize(new Dimension(200, 22));
        heightLabel.setPreferredSize(new Dimension(120, 22));
        
        
        groupLayout2.setHorizontalGroup(groupLayout2.createSequentialGroup()
            .addGroup(groupLayout2.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(heightLabel)
                .addComponent(outDirectoryLabel))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(groupLayout2.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(heightTextField)
                .addComponent(outDirectoryTextField))
            .addGroup(groupLayout2.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(outDirectoryBrowseButton)));
        groupLayout2.setVerticalGroup(groupLayout2.createSequentialGroup()
            .addGroup(groupLayout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(heightLabel)
                .addComponent(heightTextField))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(groupLayout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(outDirectoryLabel)
                .addComponent(outDirectoryTextField)
                .addComponent(outDirectoryBrowseButton))
        );
        
        Button convertButton = new Button("Convert");
        convertButton.setBounds(10, 230, 100, 22);
        Label optionalLabel = new Label("(*) Optional");
        optionalLabel.setBounds(120, 230, 200, 22);
        this.frame.add(convertButton);
        this.frame.add(optionalLabel);
        
        ConverterGUIActionListener actionListener = new ConverterGUIActionListener(
                this, this.frame,
                inputModelBrowseButton, inputMaterialDataBrowseButton, 
                outDirectoryBrowseButton, convertButton,
                inputModelTextField, inputMaterialDataTextField,
                heightTextField, outDirectoryTextField
        );
        inputModelBrowseButton.addActionListener(actionListener);
        inputMaterialDataBrowseButton.addActionListener(actionListener);
        outDirectoryBrowseButton.addActionListener(actionListener);
        convertButton.addActionListener(actionListener);
        
        inputPanelBorder.add(inputPanel);
        outputPanelBorder.add(outputPanel);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.add(inputPanelBorder);
        this.frame.add(outputPanelBorder);
        this.frame.getContentPane().setPreferredSize(new Dimension(420, 262));
        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.revalidate();
        this.frame.repaint();
    }
    
    public void destroy(){
        
    }
    
    
    void convert(ProgressWindow progressWindow, String path, int setHeight, String materialPath, String outDirectory) throws IOException{
        
        Map<String, Texture> textures = new HashMap<>();
        Triangle[] t = ObjReader.read(path, textures);
        
        MaterialSet materialSet;
        
        //read material data
        if(materialPath.trim().length() > 0){
            materialSet = MaterialSet.readMaterialSet(materialPath);
        } else {
            materialSet = new MaterialSet();
        }
        
        Vec3 size = Triangle.getMax(t).subtract(Triangle.getMin(t));
        int cubesY = setHeight;
        float width = size.x;
        float height = size.y;
        float cubeSize = height / ((float) (cubesY - 1));
        int cubesX = (int) Math.ceil(size.x / cubeSize);
        int cubesZ = (int) Math.ceil(size.z / cubeSize);
        
        System.out.println("size: " + size);
        System.out.println("Min: " + Triangle.getMin(t));
        System.out.println("Max: " + Triangle.getMax(t));
        System.out.println("Dimensions: " + cubesX + ", " + cubesY + ", " + cubesZ);
        System.out.println("Triangles: " + t.length);
        
        Vec3 min = Triangle.getMin(t);
        cubesX += 2; cubesZ += 2;
        World world = new World();
        
        int nThreads = (cubesY + 15) / 16;
        if(nThreads > MAX_THREADS) nThreads = MAX_THREADS;
        
        int layersToThread = cubesY;
        //round up to a multiple of nThreads
        if((layersToThread % nThreads) != 0){
            layersToThread += nThreads - (layersToThread % nThreads);
        }
        //dish out threads
        
        List<Object> completeLayers = new ArrayList<>();
        
        int layersPerThread = layersToThread / nThreads;
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < nThreads; i++){
            LayererThread t1 = new LayererThread(t, world, materialSet, 
                    textures, cubeSize, min, layersPerThread * i, cubesX, 
                    cubesY, cubesZ, layersPerThread, progressWindow);
            Thread th1 = t1.begin();
            threads.add(th1);
        }
        
        for(Thread th : threads){
            try{
                th.join();
            } catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
        
        if(outDirectory.endsWith("/") || outDirectory.endsWith("\\")){
            outDirectory = outDirectory.substring(0, outDirectory.length() - 1);
        }
        
        File region = new File(outDirectory + "/region");
        if(region.exists()) region.delete();
        region.mkdirs();
        
        world.save(progressWindow, outDirectory);
    }
    
    
    
    
    public static class ProgressWindow{

        public final JFrame frame;
        public final JProgressBar layersProgressBar;
        public final JProgressBar regionsProgressBar;
        public final JProgressBar chunksProgressBar;

        public ProgressWindow(){

            this.frame = new JFrame("Progress");
            JLabel l1 = new JLabel("Layers: ");
            JLabel l2 = new JLabel("Regions: ");
            JLabel l3 = new JLabel("Chunks: ");
            this.frame.setLayout(null);
            l1.setBounds(10, 10, 120, 22);
            l2.setBounds(10, 42, 120, 22);
            l3.setBounds(10, 74, 120, 22);

            this.layersProgressBar = new JProgressBar();
            this.regionsProgressBar = new JProgressBar();
            this.chunksProgressBar = new JProgressBar();
            this.layersProgressBar.setBounds(130, 10, 300, 22);
            this.regionsProgressBar.setBounds(130, 42, 300, 22);
            this.chunksProgressBar.setBounds(130, 74, 300, 22);
            this.layersProgressBar.setStringPainted(true);
            this.regionsProgressBar.setStringPainted(true);
            this.chunksProgressBar.setStringPainted(true);

            this.frame.add(l1);
            this.frame.add(l2);
            this.frame.add(l3);
            this.frame.add(this.layersProgressBar);
            this.frame.add(this.regionsProgressBar);
            this.frame.add(this.chunksProgressBar);
            this.frame.getContentPane().setPreferredSize(new Dimension(440, 106));
            this.frame.setResizable(false);
            this.frame.setVisible(true);
            this.frame.pack();
        }

        public void destroy(){
            this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
        }

    }
    
}

class ConverterGUIActionListener implements ActionListener{
    
    ConverterGUI gui;
    JFrame frame;
    Button modelBrowse;
    Button materialBrowse;
    Button outputBrowse;
    Button convert;
    
    TextField modelInput;
    TextField materialInput;
    TextField heightInput;
    TextField outputInput;

    ConverterGUIActionListener(ConverterGUI gui, JFrame frame, Button modelBrowse, Button materialBrowse, Button outputBrowse, Button convert,
            TextField modelInput, TextField materialInput, TextField heightInput, TextField outputInput){
        this.gui = gui;
        this.frame = frame;
        this.modelBrowse = modelBrowse;
        this.materialBrowse = materialBrowse;
        this.outputBrowse = outputBrowse;
        this.convert = convert;
        this.modelInput = modelInput;
        this.materialInput = materialInput;
        this.heightInput = heightInput;
        this.outputInput = outputInput;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Component c = (Component) e.getSource();
        if(c.equals(modelBrowse)){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Wavefront OBJ Files", "obj");
            chooser.setFileFilter(filter);
            int val = chooser.showOpenDialog(frame);
            if(val == JFileChooser.APPROVE_OPTION){
                modelInput.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        } else if(c.equals(materialBrowse)){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Material Definition Files", "txt");
            chooser.setFileFilter(filter);
            int val = chooser.showOpenDialog(frame);
            if(val == JFileChooser.APPROVE_OPTION){
                materialInput.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        } else if(c.equals(outputBrowse)){
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int val = chooser.showOpenDialog(frame);
            if(val == JFileChooser.APPROVE_OPTION){
                outputInput.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        } else if(c.equals(convert)){
            String path = modelInput.getText();
            String materialDefinition = materialInput.getText();
            String heightString = heightInput.getText();
            String outDirectory = outputInput.getText();
            if(path.length() == 0 || heightString.length() == 0 || outDirectory.length() == 0){
                JOptionPane.showMessageDialog(frame, "Required fields not filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int height = 0;
            try{
                height = Integer.parseInt(heightString);
            } catch(Exception ex){
                //just let height stay 0, the next if statement will take care
                //of it
            }
            if(height < 1){
                JOptionPane.showMessageDialog(frame, "Invalid height.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(height > 255){
                JOptionPane.showMessageDialog(frame, "The height exceeds 255 blocks, part of the model may get clipped.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            //check if paths exist
            if(!new File(path).isFile() || !new File(outDirectory).isDirectory()
                    || (materialDefinition.length() > 0 && !new File(materialDefinition).isFile())){
                JOptionPane.showMessageDialog(frame, "One or more files could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            ConverterGUI.ProgressWindow progressWindow = new ConverterGUI.ProgressWindow();
            final int generateHeight = height;
            
            new Thread(()->{
                try{

                    gui.convert(progressWindow, path, generateHeight, materialDefinition, outDirectory);
                    JOptionPane.showMessageDialog(frame, "Conversion success.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch(IOException ex){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ex.printStackTrace(new PrintStream(baos));
                    JOptionPane.showMessageDialog(frame, new String(baos.toByteArray()), "Error", JOptionPane.ERROR_MESSAGE);
                }
                progressWindow.destroy();
            }).start();
        }
    }
    
}
