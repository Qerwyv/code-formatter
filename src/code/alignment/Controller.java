package code.alignment;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class Controller {

    static File fileToDelete;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button pasteButton;

    @FXML
    private Button copyButton;

    @FXML
    private Button clearButton;

    @FXML
    private TextArea textAreaCode;

    @FXML
    private TextField maximumLineWidth;

    @FXML
    void showTips(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Особливості");
        alert.setHeaderText(null);
        alert.setContentText("Для швидкого виправлення коду після зміни кількості символів у рядку слід використовувати кнопку 'Вставити', але у цьому випадку " +
                "критерій к-ті символів може некоректно впливати на FXML-теги. Для їх правильного відображення натисніть клавішу 'Enter' у полі зміни кількості символів у рядку.");
        alert.showAndWait();

    }

    @FXML
    void showAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Про програму");
        alert.setHeaderText("Версія 1.1\nАвтор - Владислав Харченко, студент 2 курсу ХПІ КН ІПЗ");
        alert.setContentText("29.12.2018\tРеліз версії 1.0\n" +
                "12.01.2019\tРеліз версії 1.1\n — виправлено баг, при якому в деяких випадках кнопка 'Скопіювати' не працювала;\n — файл 'code.txt', автоматично створюваний у поточній теці при кожній вставці тексту, тепер видаляється після закриття програми.\n\n\n" +
                "\t\t\t\t\t\t\t\tt.me/Qerwyv");
        alert.showAndWait();
    }


    @FXML
    void maximumLineWidthIsChanged(ActionEvent event) {
        copyToClipboardButton(event);
        pasteFromClipboardButton(event);
        maximumLineWidth.setDisable(true);
    }


    @FXML
    void copyToClipboardButton(ActionEvent event) {
        String ctc = textAreaCode.getText().toString();
        StringSelection stringSelection = new StringSelection(ctc);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    @FXML
    void pasteFromClipboardButton(ActionEvent event) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        if (t == null)
            return;
        try {
            textAreaCode.setText((String) t.getTransferData(DataFlavor.stringFlavor));
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeTextAreaToFile();
        modifyFile("code.txt");
    }

    @FXML
    void clearAction(ActionEvent event) {
        maximumLineWidth.setDisable(false);
        textAreaCode.clear();
    }

    private void writeTextAreaToFile() {
        ObservableList<CharSequence> paragraph = textAreaCode.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(new File("code.txt")));
            while (iter.hasNext()) {
                CharSequence seq = iter.next();
                bf.append(seq);
                bf.newLine();
            }
            bf.flush();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifyFile(String filePath) {
        File fileToBeModified = new File(filePath);
        fileToDelete = fileToBeModified;
        String oldContent = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null) {
                if (line.isEmpty()) {
                    line = reader.readLine();
                    if (line == null)
                        break;
                }
                if (line.trim().length() < Integer.parseInt(maximumLineWidth.getText())) {//line.trim().length() < 20 or line.endsWith("}")
                    line = line.trim();
                    oldContent = oldContent.trim();
                    if (line.startsWith("@FXML")) {
                        oldContent += "\n" + line + " ";
                        line = reader.readLine();
                        line = line.trim();
                        oldContent += line + System.lineSeparator();
                    } else {
                        oldContent += line + System.lineSeparator();
                    }
                } else {
                    if (line.startsWith("@FXML")) {
                        line = line.trim();
                        oldContent = oldContent.trim();
                        oldContent += "\n" + line + " ";
                        line = reader.readLine();
                        line = line.trim();
                        oldContent += line + System.lineSeparator();
                    } else {
                        oldContent += line + System.lineSeparator();
                    }
                }
                line = reader.readLine();
                //System.out.println("line = " + line);
            }
            textAreaCode.setText(oldContent);
            textAreaCode.setFont(Font.font("Georgia", 16));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void initialize() {
        maximumLineWidth.setTooltip(new Tooltip("Для вирівнювання FXML тегів натисніть Enter"));
    }
}