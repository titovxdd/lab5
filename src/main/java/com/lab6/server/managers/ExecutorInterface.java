package com.lab6.server.managers;

import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.common.Sup.Pair;
import com.lab6.common.models.MusicBand;

public interface ExecutorInterface {
    ExecutionStatus runCommand(String[] userCommand, MusicBand musicBand, Pair<String, String> user);
}
