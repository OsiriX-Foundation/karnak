package org.karnak.backend.model.dicom;

import org.karnak.backend.enums.MessageFormat;
import org.karnak.backend.enums.MessageLevel;

public class Message {

  private MessageLevel level;
  private MessageFormat format;
  private String text;


  public Message(MessageLevel level, MessageFormat format, String text) {
    this.level = level;
    this.format = format;
        this.text = text;
    }

    
    public MessageLevel getLevel() {
        return level;
    }

    public void setLevel(MessageLevel level) {
        this.level = level;
    }

    public MessageFormat getFormat() {
        return format;
    }

    public void setFormat(MessageFormat format) {
        this.format = format;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}
