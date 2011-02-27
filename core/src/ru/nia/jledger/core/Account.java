package ru.nia.jledger.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private String name;

    private Account parent;
    private ArrayList<Account> children = new ArrayList<Account>();

    public Account(String name, Account parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public Account getParent() {
        return parent;
    }

    public List<Account> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Account addChild(String name) {
        Account newAccount = new Account(name, this);
        children.add(newAccount);
        return newAccount;
    }

    public Account findOrCreateChild(String name) {
        Account child = null;
        // find direct child...
        for (Account subAccount : children) {
            if (subAccount.getName().equals(name)) {
                child = subAccount;
                break;
            }
        }
        // ...or create him
        if (child == null) {
            child = addChild(name);
        }
        return child;
    }

    public Account findOrCreateIndirectChild(List<String> names) {
        if(names.isEmpty()) {
            return this;
        }

        Account directChild = findOrCreateChild(names.get(0));

        List<String> remainingNames = names.subList(1, names.size());
        return directChild.findOrCreateIndirectChild(remainingNames);
    }
}
