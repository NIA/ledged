package ru.nia.ledged.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AccountTree {
    private final Account root = new Account(null, null, this);

    public Account findOrCreateChild(boolean allowCreation, String... names) {
        if (names.length == 0) {
            throw new IllegalArgumentException("no names given");
        }
        Account current = root;

        for (String childName : names) {
            Account next = null;
            // find direct child...
            for (Account child : current.getChildren()) {
                if (child.getName().equals(childName)) {
                    next = child;
                    break;
                }
            }
            // ...or create him
            if (next == null) {
                if (allowCreation) {
                    next = new Account(childName, current, this);
                    current.children.add(next);
                } else {
                    // or say it's not found (if not allowed to create)
                    return null;
                }
            }
            current = next;
        }
        return current;
    }

    public Set<Account> getRootAccounts() {
        return root.getChildren();
    }

    private String accountToString(Account account) {
        assert account != root;

        StringBuilder sb = new StringBuilder();
        Account current = account.getParent();
        while (current != root) {
            sb.insert(0, ":");
            sb.insert(0, current.getName());
            current = current.getParent();
        }
        sb.append(account.getName());

        return sb.toString();
    }

    public static class Account {
        private AccountTree tree;
        private String name;

        private Account parent;
        private HashSet<Account> children = new HashSet<Account>();

        private Account(String name, Account parent, AccountTree tree) {
            this.name = name;
            this.parent = parent;
            this.tree = tree;
        }

        public String toString() {
            return tree.accountToString(this);
        }

        public String getName() {
            return name;
        }

        private Account getParent() {
            return parent;
        }

        public Set<Account> getChildren() {
            return Collections.unmodifiableSet(children);
        }

    }
}
