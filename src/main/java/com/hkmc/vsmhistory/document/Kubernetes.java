package com.hkmc.vsmhistory.document;

import lombok.Getter;

@Getter
public class Kubernetes {

    private Labels labels;
    private String namespace;
    private Node node;
    private Pod pod;
}
