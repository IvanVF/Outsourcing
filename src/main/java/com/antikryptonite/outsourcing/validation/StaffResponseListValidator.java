package com.antikryptonite.outsourcing.validation;

import com.antikryptonite.outsourcing.dto.response.StaffResponse;

import java.util.List;

public class StaffResponseListValidator {

    /**
     * Метод проверки правильности вода данных о команде поставщика
     */
    public static boolean validateStaffResponseList(List<StaffResponse> staffResponseList) throws NullPointerException {
        boolean isAnyMistakes = false;
        for (int i = 0; i < staffResponseList.size(); i++) {
            if (staffResponseList.get(i).getActivity().length() < 2 || staffResponseList.get(i).getActivity().length() > 100) {
                isAnyMistakes = true;
                break;
            }

            if (staffResponseList.get(i).getHeadcount() < 1 || staffResponseList.get(i).getHeadcount() > 10000) {
                isAnyMistakes = true;
                break;
            }
        }
        return isAnyMistakes;
    }

}
