package com.music.adaptor;

import com.music.constatns.AudioQuality;
import java.io.File;

public interface AudioConverter {

  File convert(File file, AudioQuality audioQuality);
}
