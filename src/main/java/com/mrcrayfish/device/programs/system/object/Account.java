package com.mrcrayfish.device.programs.system.object;

public class Account {
	private int balance;

	public Account(int balance) {
		this.balance = balance;
	}

	public int getBalance() {
		return balance;
	}

	public boolean hasAmount(int amount) {
		return amount <= balance;
	}

	public void add(int amount) {
		if (amount > 0) {
			balance += amount;
		}
	}

	public void remove(int amount) {
		balance -= amount;
		if (balance < 0) {
			balance = 0;
		}
	}

	public boolean deposit(int amount) {
		if (amount > 0) {
			balance += amount;
			return true;
		}
		return false;
	}

	public boolean withdraw(int amount) {
		if (hasAmount(amount)) {
			balance -= amount;
			return true;
		}
		return false;
	}

	public boolean pay(Account reciever, int amount) {
		if (reciever != null) {
			if (hasAmount(amount)) {
				balance -= amount;
				reciever.balance += amount;
				return true;
			}
		}
		return false;
	}
}
