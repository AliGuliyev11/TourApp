package com.mycode.tourapptelegrambot.enums;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote This enum for telegram bot state
 * If bot state is FILLING_TOUR program continue
 * Else if bot state is VALIDATION program ask re-enter input,because user input not matched with regex
 */


public enum BotState {
    FILLING_TOUR,
    VALIDATION,
}
