import javax.swing.*;
import java.awt.*;
import java.awt.event.*;    
import javax.swing.JFileChooser; //fileChooser for saveas feature
import java.io.File; //file manip
import java.io.IOException;
import javax.swing.JOptionPane; //used for alert-message window feature
import java.io.FileWriter; //file writer for save feature
import java.lang.Thread;

class launch implements ActionListener{
          JFrame frame;
          boolean unsaved; //checks if current file has unsaved changes
          File thisFile; //current opened file object
          String textWhileSaving, currText; //temporary object to store text in text area - to manipulate unsaved changes
          JTextArea textArea;
          JMenuItem New, Save, SaveAs, Open, Copy, Paste, Cut, SelectAll,FontStyle, FontSize;
          boolean goTo__SaveAs = false; //wild card for the save function to escort the control to saveas whenever necessary
          
          launch(){
          //container frame
          frame = new JFrame("Noah Editor");
          frame.setExtendedState(JFrame.MAXIMIZED_BOTH); //make container full screen
          
          //menu bar starts
          JMenuBar mb=new JMenuBar();
          JMenu menu_file = new JMenu("File");
          New = new JMenuItem("New");
          Save = new JMenuItem("Save");
          SaveAs = new JMenuItem("Save As");
          Open = new JMenuItem("Open");
          SaveAs.addActionListener(this);
          Save.addActionListener(this);
          New.addActionListener(this);
          menu_file.add(New);
          menu_file.add(Save);
          menu_file.add(SaveAs);
          menu_file.add(Open);
          mb.add(menu_file);
          
          JMenu menu_edit = new JMenu("Edit");
          Copy = new JMenuItem("Copy");
          Cut = new JMenuItem("Cut");
          Paste = new JMenuItem("Paste");
          SelectAll = new JMenuItem("Select All");  
          //add action listener
          Copy.addActionListener(this);
          Cut.addActionListener(this);
          Paste.addActionListener(this);
          SelectAll.addActionListener(this);                    
          menu_edit.add(Copy);
          menu_edit.add(Cut);
          menu_edit.add(Paste);
          menu_edit.add(SelectAll);
          mb.add(menu_edit);
          
          JMenu menu_pref = new JMenu("Preferences");
          FontSize = new JMenuItem("Font Size");
          FontStyle = new JMenuItem("Font Style");
          menu_pref.add(FontSize);
          menu_pref.add(FontStyle);
          mb.add(menu_pref);
          
          JMenu menu_close = new JMenu("Close");
          mb.add(menu_close);
          frame.setJMenuBar(mb);
          //menu bar ends

          //text area
          textArea = new JTextArea(16, 58);
          textArea.setFont(new Font("Serif", Font.ITALIC, 16));
          textArea.setLineWrap(true);
          
          
          //check for unsaved changes in text area using threads
          textWhileSaving = "#$"; 
          currText = "#$";
          Thread myThread = new Thread(new Runnable(){
          @Override
          public void run(){
          while(true){
                    //if currrent text is same as textWhileSaving -> file is saved; else file's unsaved
                    if(!textWhileSaving.equals(currText))
                              unsaved = true;
                    else
                              unsaved = false;
                    
                    //if text area isn't empty, meaning it's not null, update current text.
                    //Solved null pointer exception using this.           
                    if(textArea.getText() != "" && textArea.getText() != null && textArea.getText() != " "){
                              currText = textArea.getText();
                    }
                              
                    if(unsaved){
                              String tempTitle = frame.getTitle();
                              if(tempTitle.charAt(tempTitle.length() - 1) != '*')
                                        frame.setTitle(tempTitle + "*");
                    }
                    else{
                              String tempTitle = frame.getTitle();
                              if(tempTitle.charAt(tempTitle.length() - 1) == '*')
                                        frame.setTitle(tempTitle.substring(0, tempTitle.length() - 1));
                    }
               }
               }     
          });
          myThread.start(); //start thread
          
          
          JScrollPane scroll = new JScrollPane(textArea);
          scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          
          frame.add(scroll);
          frame.setVisible(true); 
          }
          
          public void actionPerformed(ActionEvent e) {    
          //actions for edit menu
          if(e.getSource() == Cut)    
                    textArea.cut();    
          else if(e.getSource() == Paste)    
                    textArea.paste();    
          else if(e.getSource() == Copy)    
                    textArea.copy();    
          else if(e.getSource() == SelectAll)    
                    textArea.selectAll();  
          
          
          if(e.getSource() == Save){
                    if(frame.getTitle().length() <= 12) //if file to be saved as is not selected, the title will only be "Noah Editor" or "Noah Editor*". If that's the case, escort to saveas first. 
                              goTo__SaveAs = true; //set wild card
                    
                    else
                              saveToFile(thisFile.getAbsolutePath()); 
          }
          
          //actions for file menu
          if(e.getSource() == SaveAs || goTo__SaveAs){
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Noah editor -- save file as");
                    int fileChooserStatus = fileChooser.showSaveDialog(frame);
                    if(fileChooserStatus == JFileChooser.APPROVE_OPTION){
                              File fileSelected = fileChooser.getSelectedFile();
                              thisFile = fileSelected;
                              //call createFile funct to create a new file. 
                              int createFileStatus = createFile(fileSelected.getAbsolutePath());
                              if(createFileStatus == 1){
                                        //update window title
                                        frame.setTitle("Noah Editor --" + fileSelected.getName() + " (" +fileSelected.getAbsolutePath() + ")");
                                        //call save func
                                        saveToFile(fileSelected.getAbsolutePath());
                              }
                              else if(createFileStatus == 0)
                                        alert(fileSelected.getName() + " already exists.", "" );                                                  
                    }
                    
                    goTo__SaveAs = false; //disable wild card   
               }
          }
          
          public void saveToFile(String fAbsPath){
                    try{
                              FileWriter writer = new FileWriter(fAbsPath);
                              writer.write(textArea.getText());
                              writer.close();
                              unsaved = false;
                              if(textArea.getText() != null)
                                        textWhileSaving = textArea.getText();
                    }
                    catch (IOException e){
                              alert(e.getStackTrace().toString(), ": FAILED TO WRITE TO FILE"); 
                    }
                    
          }
          
          public static void alert(String message, String alertWindowTitle){
                   JOptionPane.showMessageDialog(null, message, "ALERT" + alertWindowTitle, JOptionPane.INFORMATION_MESSAGE); 
          } 
          
          public static int createFile(String fAbsPath){
                    File file = new File(fAbsPath);
                    try{
                    if(file.createNewFile())
                              return 1;
                    else 
                              return 0;
                    }
                    catch(IOException e){
                              alert(e.getStackTrace().toString(), ": FAILED TO CREATE FILE");
                              return -1;
                    }
                              
          }     
}



public class Noah{
public static void main(String s[]){
          launch window = new launch();
}
}
