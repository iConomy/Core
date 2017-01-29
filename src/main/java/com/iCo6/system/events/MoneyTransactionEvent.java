package com.iCo6.system.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * Created by JOO200 on 28.01.2017.
 * iCo6 by Team iCo6 add by JOO200
 *
 * copyright Copyright Nexua LLC (C) 2015
 * @author Nijikokun nijikokun@gmail.com
 * @author SpaceManiac
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Event is fired, when a player pays another player with "/money pay <Name> <amount> <reason>"
 *
*
*/
public class MoneyTransactionEvent
        extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private Player sender;
    private String receiver;
    private double money;
    private String message;
    private boolean cancelled;
    private State state;
    private boolean sendMessage;

    public MoneyTransactionEvent(Player sender, String receiver,
                                 double money, String message, State state) {
        this.sender = sender;
        this.receiver = receiver;
        this.money = money;
        this.message = message;
        this.state = state;
        this.sendMessage = true;
    }

    /**
     *
     * @return Handlerlist
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     *
     * @return Sender of the Payment
     */
    public Player getSender() {
        return sender;
    }

    /**
     *
     * @return Receiver of the Payment
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     *
     * @return State of the Receivement.
     */
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getMoney() {
        return money;
    }

    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }

    public boolean sendMessage() {
        return this.sendMessage;
    }

    /**
     * Optionale Nachricht
     *
     * @return Nachricht oder null, falls keine
     * Nachricht angegeben wurde.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "Sender: " + sender + ", Receiver: " + receiver +
                " Amount: " + money + " Message: " + message + " State: " + state.toString();
    }

    public static enum State {
        COMPLETE,
        ERROR_ACCOUNT,
        SELF_PAYMENT,
        NO_MONEY,
        OTHER;

        private State() {

        }
    }
}
