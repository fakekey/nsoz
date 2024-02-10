package com.nsoz.model;

import com.nsoz.network.Message;

public interface IChat {
    void read(Message ms);

    void wordFilter();

    void send();
}
