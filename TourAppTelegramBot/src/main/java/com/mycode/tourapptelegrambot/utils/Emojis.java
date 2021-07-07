package com.mycode.tourapptelegrambot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Emojis {
    SPARKLES(EmojiParser.parseToUnicode(":sparkles:")),
    SCROLL(EmojiParser.parseToUnicode(":scroll:")),
    MAGE(EmojiParser.parseToUnicode(":mage:")),
    Fire(EmojiParser.parseToUnicode(":fire:")),
    Question(EmojiParser.parseToUnicode(":question:")),
    Check(EmojiParser.parseToUnicode("✅")),
    Times(EmojiParser.parseToUnicode("❌")),
    HourGlass(EmojiParser.parseToUnicode(":hourglass:")),
    Plus(EmojiParser.parseToUnicode("➕")),
    SUCCESS_MARK(EmojiParser.parseToUnicode(":white_check_mark:")),
    Azerbaijan(EmojiParser.parseToUnicode(":az:")),
    Russian(EmojiParser.parseToUnicode(":ru:")),
    English(EmojiParser.parseToUnicode(":gb:")),
    Beach(EmojiParser.parseToUnicode(":umbrella_on_ground:")),
    Clock(EmojiParser.parseToUnicode(":calendar:"));




    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
