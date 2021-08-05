package com.mycode.tourapptelegrambot.enums;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote telegram bot convert user input message to entity filed dynamically
 * Because of this,programmer must declare some hints for this program
 * And when programmer create question action he/she must enter QuestionType field
 * This enum works for field setter and creating inline keyboard button
 */

public enum QuestionType {
    Button,
    Free_Text,
    Button_Free_Text,
    Button_Prediction,
    Button_Calendar,
    Button_Numeric,
    Button_Keyboard
}
