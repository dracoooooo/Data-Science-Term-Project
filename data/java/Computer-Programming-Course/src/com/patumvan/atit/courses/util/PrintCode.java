package com.patumvan.atit.courses.util;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;


public class PrintCode {
public static String padded(int x,int pad)
{
        String r="";
    for (int i=0; i<pad-(Integer.toString(x).length()); i++ )
    r+="0";
return r+x+":";
}

    public static void main(String[] args) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        Transferable tran = null;
        try {
            tran = clipboard.getContents(null);

        } catch (IllegalStateException ex) {
            System.out.println("The clipboard is unavailable.");
            return;
        }
        if (tran != null
                && tran.isDataFlavorSupported(DataFlavor.stringFlavor)) {

            try {

                String clipboardContent =
                        (String) tran.getTransferData(DataFlavor.stringFlavor);

                //System.out.println(clipboardContent);
                String str;

                BufferedReader reader = new BufferedReader(
                        new StringReader(clipboardContent));
                //JTextPane edit = new JTextPane();
                //edit.setContentType("text/html");
        //edit.setText("<b>hello</b> world<br>\n<i>ja</i>");
        //edit.selectAll();
        //edit.copy();

                try {
                    int line = 1;
                    while ((str = reader.readLine()) != null) {
                       
                            System.out.printf("%s %s\n",padded(line++,2),str);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }



            } catch (UnsupportedFlavorException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
