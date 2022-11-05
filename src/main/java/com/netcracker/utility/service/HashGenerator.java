package com.netcracker.utility.service;

import lombok.Value;

public interface HashGenerator {
    NonRepeatableHash getNonRepeatableHash(String fileName, int size);

    // overload withdefault size of seven
    NonRepeatableHash getNonRepeatableHash(String fileName);


    // overload withdefault size of seven and takes not file name
    NonRepeatableHash getNonRepeatableHash();
}


