package com.scmspain.controller.validator;

/**
 * Created by xgimenez on 29/9/17.
 */
public interface ICommandValidator<C> {

    /**
     * Validate command input data. If error detected {@link IllegalArgumentException} must be thrown.
     *
     * @param command to be validated
     */
    void validate(C command);
}
