import javax.swing.*;
import java.awt.*;
import java.awt.event.*;    
import javax.swing.JFileChooser; //fileChooser for saveas feature
import java.io.File; //file manip
import java.io.IOException;
import javax.swing.JOptionPane; //used for alert-message window feature
import java.io.FileWriter; //file writer for save feature
import java.lang.Thread;
import java.util.Scanner;
import javax.swing.text.Highlighter; //highlight search string
import javax.swing.text.DefaultHighlighter;
//import TextLineNumber; no need to import -- local class in same directory/package (I do not own TextLineNumber class -- borrowed from the internet )

class launch implements ActionListener{
          JFrame frame;
          boolean unsaved; //checks if current file has unsaved changes
          File thisFile; //current opened file object
          String textWhileSaving, currText; //temporary object to store text in text area - to manipulate unsaved changes
          JTextArea textArea;
          JMenuItem New, Save, SaveAs, Open, Copy, Paste, Cut, SelectAll,FontStyle, FontSize, FontType, Quit, FindAndReplaceAll, FindAll, ClearHighlighted, ShortCuts, Doc;
          int doExit; //should the program terminate?; 1 if yes, 0 if no.
          boolean goTo__SaveAs = false; //wild card for the save function to escort the control to saveas whenever necessary
          boolean goTo__Save = false; //wild card to go to Save function (currently unused)
          //note- for both of the above wild cards, we retail there value to false at the end of there respective functions they are wildcarded to.
          int currFontSize = 15; //current font size --mutatable
          int currFontType = Font.PLAIN; //font type can be plain, bold, italic --mutatable
          String currFontStyle =  "Serif"; //font style can be Times New Roman, SansSerif, Monospaced --mutatable
          
          //micellaneous declarations
          Object selectedFontSize = 15, selectedFontType = Font.PLAIN, selectedFontStyle = "Serif"; //defaults
          
          public void launchNoah(){
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
          Quit = new JMenuItem("Quit");
          Quit.addActionListener(this);
          SaveAs.addActionListener(this);
          Save.addActionListener(this);
          New.addActionListener(this);
          Open.addActionListener(this);
          menu_file.add(New);
          menu_file.add(Open);
          menu_file.add(Save);
          menu_file.add(SaveAs);
          menu_file.add(Quit);
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
          FontType = new JMenuItem("Font Type");
          FontStyle = new JMenuItem("Font Style");
          FontSize.addActionListener(this);
          FontType.addActionListener(this);
          FontStyle.addActionListener(this);
          menu_pref.add(FontSize);
          menu_pref.add(FontType);
          menu_pref.add(FontStyle);
          mb.add(menu_pref);
          
          JMenu menu_search = new JMenu("Search");
          FindAll = new JMenuItem("Find all");
          FindAndReplaceAll = new JMenuItem("Find and Replace all");
          ClearHighlighted = new JMenuItem("Clear highlight on text");
          FindAll.addActionListener(this);
          FindAndReplaceAll.addActionListener(this);
          ClearHighlighted.addActionListener(this);
          menu_search.add(FindAll);
          menu_search.add(FindAndReplaceAll);
          menu_search.add(ClearHighlighted);          
          mb.add(menu_search);
          
          JMenu menu_help = new JMenu("Help");
          ShortCuts = new JMenuItem("Keyboard Shortcuts");
          Doc = new JMenuItem("Noah Documentation");
          ShortCuts.addActionListener(this);
          Doc.addActionListener(this);
          menu_help.add(ShortCuts);
          menu_help.add(Doc);
          mb.add(menu_help);
          frame.setJMenuBar(mb);
          //menu bar ends

          //text area
          textArea = new JTextArea(16, 58);
          textArea.setFont(new Font(currFontStyle, currFontType, currFontSize));
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
          
          TextLineNumber lineNumber = new TextLineNumber(textArea);
          scroll.setRowHeaderView(lineNumber);
          
          frame.add(scroll);
          frame.setVisible(true); 
          
          //add keyListener to listen to ctrl+s (save command)
          textArea.addKeyListener(new java.awt.event.KeyAdapter() {
                    @Override
                    public void keyPressed(java.awt.event.KeyEvent evt) {
                              if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_S){
                                        Save.doClick(); //stimulate Save event on incurring ctrl+s
                              }
                              else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_F){
                                        FindAll.doClick(); //stimulate FindAll event on incurring ctrl+f
                              }
                              else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_H){
                                        FindAndReplaceAll.doClick(); //stimulate findAndReplaceAll event on incurring ctrl+h
                              }
                              else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_W){
                                        Quit.doClick(); //override (system) close event on incurring ctrl+x
                              }
                    }
               });
               
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
          
          else if(e.getSource() == New){
                    int abortNew = 0;
                    if(unsaved == true && frame.getTitle().length() > 12){ //if saveas is done, but file unsaved
                              int confirmStatus = confirmMessageAlert("Unsaved changes found, save file?");
                              if(confirmStatus == JOptionPane.YES_OPTION)
                                        saveToFile(thisFile.getAbsolutePath());
                    }
                    else if(unsaved == true && frame.getTitle().length() <= 12){ //if saveas is not done, and save is also not done
                              int confirmStatus = confirmMessageAlert("[File not saved, use <save as>],  Continue without saving?");
                              if(confirmStatus == JOptionPane.NO_OPTION)
                                        abortNew = 1; //dont create new window, stay in the current one. 
                    }
                    
                    if(abortNew == 0){ //if there's no need to abort new option -- launch new window. Else stay.
                              launch window = new launch();
                              window.launchNoah();
                    }
          }  
               
          if(e.getSource() == Save || goTo__Save){
                    if(frame.getTitle().length() <= 12) //if file to be saved as is not selected, the title will only be "Noah Editor" or "Noah Editor*". If that's the case, escort to saveas first. 
                              goTo__SaveAs = true; //set wild card
                    
                    else
                              saveToFile(thisFile.getAbsolutePath()); 
                    
                    //disable goTo__Save wildcard
                    goTo__Save = false;
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
               
               else if(e.getSource() == Open){
                    int goFlag = 1; //this flag is 1 if Open option has to be performed, else 0
                    if(unsaved){ //if current file unsaved, notify to either continue without saving or satying.
                              int confirmStatus = confirmMessageAlert("Current opened file unsaved. Continue without saving?"); 
                              if(confirmStatus == JOptionPane.NO_OPTION)
                                        goFlag = 0; //if user doesnt wants to open new file without saving the current unsaved file, Open option shouldnt be performed
                    }          
                    
                    if(goFlag == 1){ //if Open option authorised, perform it.                    
                              JFileChooser fileChooser = new JFileChooser();
                              fileChooser.setDialogTitle("Noah editor -- open file");
                              int fileChooserStatus = fileChooser.showOpenDialog(frame);
                              if(fileChooserStatus == JFileChooser.APPROVE_OPTION){
                                        File selectedFileToOpen = fileChooser.getSelectedFile();
                                        try{
                                        //read from file and put it in textArea
                                        Scanner reader = new Scanner(selectedFileToOpen);
                                        String data = "";
                                        while(reader.hasNextLine()){
                                                  data += reader.nextLine();
                                        }
                                        textArea.setText(data);
                                        
                                        thisFile = selectedFileToOpen; //update current file to opened file
                                        //update window title
                                        frame.setTitle("Noah Editor --" + selectedFileToOpen.getName() + " (" +selectedFileToOpen.getAbsolutePath() + ")");
                                        }
                                        
                                        catch (Exception eee){
                                                  alert("FILE NOT FOUND", "");
                                        }
                              }
                    }
               }
               
               else if(e.getSource() == FontSize){
                    Integer fontSizes[] = new Integer[91];
                    for(int i = 0; i < 91; i++)
                              fontSizes[i] = i + 10; //font sizes ranges from 10-100
                    JComboBox fontSizeList = new JComboBox(fontSizes);
                    fontSizeList.setSelectedItem(selectedFontSize);
                    JOptionPane.showMessageDialog(frame, fontSizeList);
                    selectedFontSize = fontSizeList.getSelectedItem();
                    currFontSize = (int)selectedFontSize;
                    textArea.setFont(new Font(currFontStyle, currFontType, currFontSize)); //update preference
               }
               
               else if(e.getSource() == FontType){
                    String fontTypes[] = {"PLAIN", "BOLD", "ITALIC", "BOLD-ITALIC"};
                    JComboBox fontTypeList = new JComboBox(fontTypes);
                    fontTypeList.setSelectedItem(selectedFontType);
                    JOptionPane.showMessageDialog(frame, fontTypeList);
                    selectedFontType = fontTypeList.getSelectedItem();
                    
                    if(selectedFontType.toString().equals("PLAIN"))
                              currFontType = Font.PLAIN;
                    else if(selectedFontType.toString().equals("ITALIC"))
                              currFontType = Font.ITALIC;
                    else if(selectedFontType.toString().equals("BOLD"))
                              currFontType = Font.BOLD;
                    else
                              currFontType = Font.BOLD | Font.ITALIC;    
                                     
                    textArea.setFont(new Font(currFontStyle, currFontType, currFontSize)); //update preference
               }
               
               else if(e.getSource() == FontStyle){
                    String fontStyles[] = {"Times New Roman", "SansSerif", "Monospaced", "Serif"};
                    JComboBox fontStyleList = new JComboBox(fontStyles);
                    fontStyleList.setSelectedItem(selectedFontStyle); //the arg must be a object not a str or int. So did this.
                    JOptionPane.showMessageDialog(frame, fontStyleList);
                    selectedFontStyle = fontStyleList.getSelectedItem();
                    currFontStyle = selectedFontStyle.toString();
                    textArea.setFont(new Font(currFontStyle, currFontType, currFontSize)); //update preference
               }
               
               else if(e.getSource() == Quit){
               doExit = 1;
                    if(unsaved){
                              int confirmStatus = confirmMessageAlert("Unsaved changes found. Quit without saving?");
                              if(confirmStatus == JOptionPane.NO_OPTION)
                                        doExit = 0;           
                    }
                    if(doExit == 1)
                              frame.dispose(); //close window
                              
               }
               
               else if(e.getSource() == FindAll){
               try{
                    String textAreaAsString = textArea.getText();
                    int textAreaLen = textAreaAsString.length();
                    String textToFind = JOptionPane.showInputDialog(frame, "Enter text to find", "Find All", JOptionPane.INFORMATION_MESSAGE);
                    int textToFindLen = textToFind.length();
                    if(textToFindLen > 0 && textAreaLen > 0){//textarea and string to search is not empty
                              int highlight_startIndex, indexOf_startPoint = 0; //more than one occurance can be highlighted now
                              Highlighter h = textArea.getHighlighter();
                              h.removeAllHighlights();
                              while((highlight_startIndex = textAreaAsString.indexOf(textToFind, indexOf_startPoint)) != -1){
                                        h.addHighlight(highlight_startIndex, highlight_startIndex + textToFindLen, DefaultHighlighter.DefaultPainter);
                                        indexOf_startPoint = highlight_startIndex + textToFindLen + 1; //probe next occurance from point beyond.
                              }
                    }
                    }
                    catch(Exception eeee){
                              //do nothing when cancel is pressed in FindAll input window
                    }                
                                   
               }
               
               else if(e.getSource() == ClearHighlighted){
                    Highlighter h = textArea.getHighlighter();
                    h.removeAllHighlights();
               }
               
               else if(e.getSource() == FindAndReplaceAll){
                    JTextField textToFind = new JTextField();
                    JTextField textToReplace = new JTextField();
                    Object[] encapsulatedTextFields = {
                              "Enter text to find", textToFind,
                              "Enter text to replace", textToReplace
                    };
                    int status = JOptionPane.showConfirmDialog(frame, encapsulatedTextFields, "Find and Replace All", JOptionPane.OK_CANCEL_OPTION);
                    if(status == JOptionPane.OK_OPTION && textToFind.getText().length() > 0){
                              textArea.setText(textArea.getText().replaceAll(textToFind.getText(), textToReplace.getText()));
                    }
               }
               
               else if(e.getSource() == ShortCuts){
                    alert("save: ctrl+s \n find (all): ctrl+f \n find and replace (all): ctrl+h \n quit: ctrl+w \n select all: ctrl+a", ": Keyboard Shortcuts premitive to Noah");
               }
               
               else if(e.getSource() == Doc){
                    alert("Coming Soon...", ": Noah Documentation");
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
          
          public  void alert(String message, String alertWindowTitle){
                   JOptionPane.showMessageDialog(null, message, "ALERT" + alertWindowTitle, JOptionPane.INFORMATION_MESSAGE); 
          } 
          
          public  int confirmMessageAlert(String message){
                    int confirmStatus = JOptionPane.showConfirmDialog(frame, message, "Confirm Action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    return confirmStatus;
          } 
          
          public  int createFile(String fAbsPath){
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
          window.launchNoah();
}
}
