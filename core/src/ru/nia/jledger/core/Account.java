package ru.nia.jledger.core;

import java.util.*;

public class Account {
    private String name;

    private Account parent;
    private HashSet<Account> children = new HashSet<Account>();

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

    public Set<Account> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public Account findOrCreateChild(String... names) {
        Account child = this;

        for (String childName : names) {
            child = child.findOrCreateDirectChild(childName);
        }
        return child;
    }

    Account addChild(String name) {
        Account newAccount = new Account(name, this);
        children.add(newAccount);
        return newAccount;
    }

    Account findOrCreateDirectChild(String name) {
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
}
