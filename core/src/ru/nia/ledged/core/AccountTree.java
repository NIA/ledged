package ru.nia.ledged.core;

import java.util.*;

public class AccountTree {
    private final Account root = new Account(null, null, this);
    public static final String ACCOUNT_SEPARATOR = ":";

    private Account findOrCreateAccount(boolean allowCreation, String name) {
        if (name.length() == 0) {
            throw new IllegalArgumentException("no names given");
        }
        String[] names = name.split(ACCOUNT_SEPARATOR);
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

    public Account findAccount(String name) {
        return findOrCreateAccount(false, name);
    }

    public Account findOrCreateAccount(String name) {
        return findOrCreateAccount(true, name);
    }

    public Set<Account> getRootAccounts() {
        return root.getChildren();
    }

    private String accountToString(Account account) {
        assert account != root;

        StringBuilder sb = new StringBuilder();
        Account current = account.getParent();
        while (current != root) {
            // TODO: use constant
            sb.insert(0, ":");
            sb.insert(0, current.getName());
            current = current.getParent();
        }
        sb.append(account.getName());

        return sb.toString();
    }

    public List<Account> filterAccounts(String constraint) {
        Set<Account> setToFilter;
        if (constraint.contains(ACCOUNT_SEPARATOR)) {
            int sepPos = constraint.lastIndexOf(ACCOUNT_SEPARATOR);
            String parentName = constraint.substring(0, sepPos);
            Account parent = findAccount(parentName);
            if (parent != null) {
                setToFilter = parent.getChildren();
            } else {
                setToFilter = Collections.emptySet();
            }
        } else {
            setToFilter = getRootAccounts();
        }

        List<Account> filtered = new ArrayList<Account>();
        for (Account a : setToFilter) {
            if(a.toString().startsWith(constraint)) {
                filtered.add(a);
            }
        }
        return filtered;
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
