package application.menu;

import application.rdg.*;
import application.ts.BankException;

import java.math.BigDecimal;
import java.sql.SQLException;

public class ExchangeRateMenu extends Menu {
    public ExchangeRateMenu() {
        super();
    }

    private void printRate() throws SQLException {
        System.out.println("________________");
        System.out.format("| %3s %5s %5s %5s\n", "ID", "Z", "DO", "KURZ");
        for (ExchangeRate elem : ExchangeRateFinder.getInstance().findAll()) {
            System.out.format("| %3s %5s %5s %5s\n", elem.getId(), elem.getCurrencyFrom(), elem.getCurrencyTo(), elem.getAmount().toString());
        }
        System.out.println("________________");
    }

    private void createRate() throws SQLException, BankException {
        String currency;
        String currencyTo;
        BigDecimal amount;

        System.out.println("Zadajte menu:");
        currency = getInput().toUpperCase();
        if(CurrencyFinder.getInstance().findByCode(currency) == null)
            throw new BankException("Dana mena neexistuje");

        System.out.println("Zadajte druhu menu:");
        currencyTo = getInput().toUpperCase();
        if(CurrencyFinder.getInstance().findByCode(currencyTo) == null)
            throw new BankException("Dana mena neexistuje");

        if(ExchangeRateFinder.getInstance().findByCurrency(currency, currencyTo) != null) {
            throw new BankException("Dany kurz uz existuje");
        }

        System.out.println("Zadajte kurz:");
        try {
            amount = new BigDecimal(getInput());
        } catch (Exception e) {
            throw new BankException("Zadajte platny kurz");
        }

        new ExchangeRate()
                .setCurrencyFrom(currency)
                .setCurrencyTo(currencyTo)
                .setAmount(amount)
                .insert();

        System.out.println("Prevodovy kurz uspesne vytvoreny");
    }

    private void updateRate() throws BankException, SQLException {
        String currency;
        String currencyTo;
        BigDecimal amount;
        ExchangeRate exchangeRate;

        System.out.println("Zadajte menu:");
        currency = getInput().toUpperCase();
        if(CurrencyFinder.getInstance().findByCode(currency) == null)
            throw new BankException("Dana mena neexistuje");

        System.out.println("Zadajte druhu menu:");
        currencyTo = getInput().toUpperCase();
        if(CurrencyFinder.getInstance().findByCode(currencyTo) == null)
            throw new BankException("Dana mena neexistuje");

        System.out.println("Zadajte kurz:");
        try {
            amount = new BigDecimal(getInput());
        } catch (Exception e) {
            throw new BankException("Zadajte platny kurz");
        }

        exchangeRate = ExchangeRateFinder.getInstance().findByCurrency(currency, currencyTo);
        if(exchangeRate == null) {
            throw new BankException("Dany kurz neexistuje");
        }
        exchangeRate.setAmount(amount).update();

        System.out.println("Prevodovy kurz uspesne aktualizovany");
    }

    private void deleteRate() throws BankException, SQLException {
        String currency;
        String currencyTo;
        BigDecimal amount;
        ExchangeRate exchangeRate;

        System.out.println("Zadajte menu:");
        currency = getInput().toUpperCase();
        if(CurrencyFinder.getInstance().findByCode(currency) == null)
            throw new BankException("Dana mena neexistuje");

        System.out.println("Zadajte druhu menu:");
        currencyTo = getInput().toUpperCase();
        if(CurrencyFinder.getInstance().findByCode(currencyTo) == null)
            throw new BankException("Dana mena neexistuje");

        exchangeRate = ExchangeRateFinder.getInstance().findByCurrency(currency, currencyTo);
        if(exchangeRate == null) {
            throw new BankException("Dany kurz neexistuje");
        }
        exchangeRate.delete();

        System.out.println("Prevodovy kurz uspesne vymazany");
    }

    public void execute() throws SQLException, BankException {
        boolean exit = false;
        String idNumber;
        int id;
        User user;

        do {
            try {
                System.out.println("---------");
                System.out.println("Prevodovy kurz:");
                System.out.println("1. Vypis zoznam prevodovych kurzov");
                System.out.println("2. Pridaj prevodovy kurz");
                System.out.println("3. Aktualizuj prevodovy kurz");
                System.out.println("4. Vymaz prevodovy kurz");

                switch (getInput()) {
                    case "1":
                        printRate();
                        break;

                    case "2":
                        createRate();
                        break;

                    case "3":
                        updateRate();
                        break;

                    case "4":
                        deleteRate();
                        break;

                    case "x":
                        exit = true;
                        break;

                    default:
                        System.out.println("Zla moznost");
                        break;
                }
            } catch (Exception e) {
                printError(e.getMessage());
            }
        } while(!exit);
    }

}
