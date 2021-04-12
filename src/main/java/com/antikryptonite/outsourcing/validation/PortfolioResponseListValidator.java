package com.antikryptonite.outsourcing.validation;

import com.antikryptonite.outsourcing.dto.response.PortfolioResponse;

import java.util.List;

public class PortfolioResponseListValidator {

    /**
     * Метод проверки правильности вода данных о проектах поставщика
     */
    public static boolean validatePortfolioResponseList(List<PortfolioResponse> portfolioResponseList) throws NullPointerException {

        boolean isAnyMistakes = false;

        for ( int i = 0; i < portfolioResponseList.size(); i++) {
            if (portfolioResponseList.get(i).getCustomer().length() < 1 || portfolioResponseList.get(i).getCustomer().length() > 200) {
                isAnyMistakes = true;
                break;
            }

            if (portfolioResponseList.get(i).getDescription().length() < 1 || portfolioResponseList.get(i).getDescription().length() > 15000) {
                isAnyMistakes = true;
                break;
            }

            if (portfolioResponseList.get(i).getUrl().length() < 1 || portfolioResponseList.get(i).getUrl().length() > 10000) {
                isAnyMistakes = true;
                break;
            }
        }

        return isAnyMistakes;

    }

}
