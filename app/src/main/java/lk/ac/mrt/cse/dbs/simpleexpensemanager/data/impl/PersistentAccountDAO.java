package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends DB_Helper implements AccountDAO {
    private List<String> accNos;
    private List<Account> accList;
    private DB_Helper db_helper;

    public PersistentAccountDAO(Context context) {
        super(context);
        this.accNos = new ArrayList<String>();
        this.accList = new ArrayList<Account>();
        this.db_helper = new DB_Helper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        this.accNos = new ArrayList<String>();

        String sqlquery = "SELECT accountNo FROM accounts";


        Cursor cursor =db_helper.fetchData(sqlquery);
        if (cursor.moveToFirst()){

            do{
                this.accNos.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        cursor.close();

        return this.accNos;
    }

    @Override
    public List<Account> getAccountsList() {
        this.accList = new ArrayList<Account>();

        String sql = "SELECT * FROM accounts";

        Cursor cursor = db_helper.fetchData(sql);

        if (cursor.moveToFirst()){

            do{
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getInt(3);

                Account account = new Account(accountNo,bankName,accountHolderName,balance);
                this.accList.add(account);


            }while(cursor.moveToNext());
        }

        cursor.close();

        return this.accList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        String query = "SELECT * FROM accounts WHERE accountNo = '"+ accountNo + "' ;";

        Cursor cursor = db_helper.fetchData(query);
        Account account_tuple = null;

        String bankName = cursor.getString(1);
        String accountHolderName = cursor.getString(2);
        double balance = cursor.getInt(3);

        account_tuple = new Account(accountNo,bankName,accountHolderName,balance);

        cursor.close();
        return account_tuple;

    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());

        database.insert("accounts", null, contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String query = "DELETE FROM accounts WHERE accountNo = '"+ accountNo + "' ;";
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(query);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT balance FROM accounts WHERE accountNo = '"+ accountNo +"' ;";
        Cursor cursor = db_helper.fetchData(query);
        cursor.moveToFirst();
        double balance = cursor.getDouble(0);
        switch(expenseType){
            case EXPENSE:
                balance  = balance-amount;
                break;
            case INCOME:
                balance  = balance+amount;
                break;
        }

        String updateQuery = "UPDATE accounts SET balance = "+ balance +" WHERE accountNo = '"+accountNo+"' ;";
        db_helper.updateData(updateQuery);
        cursor.close();
        database.close();
    }
}
