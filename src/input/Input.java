package input;

import javax.swing.*;

public class Input
{
    public static String popupDialogWithOptions(String prompt, String[] options)
    {
        if (options.length == 0)
            throw new UnsupportedOperationException("Can't get an answer from 0 options.");

        Object selectedOption = JOptionPane.showInputDialog(
                null,
                prompt,
                "MiningBot",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        return (String) selectedOption;
    }
}
