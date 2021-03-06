/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibatis.jpetstore.presentation;

import com.ibatis.jpetstore.domain.Account;
import com.ibatis.jpetstore.domain.Order;
import com.ibatis.jpetstore.service.AccountService;
import com.ibatis.jpetstore.service.OrderService;
import com.ibatis.struts.ActionContext;
import com.ibatis.struts.BaseBean;
import com.ibatis.common.util.PaginatedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class OrderBean extends BaseBean {

  /* Constants */

  private static final AccountService accountService = AccountService.getInstance();
  private static final OrderService orderService = OrderService.getInstance();

  private static final List CARD_TYPE_LIST;

  /* Private Fields */

  private Order order;
  private int orderId;
  private boolean shippingAddressRequired;
  private boolean confirmed;
  private PaginatedList orderList;
  private String pageDirection;

  /* Static Initializer */

  static {
    List cardList = new ArrayList();
    cardList.add("Visa");
    cardList.add("MasterCard");
    cardList.add("American Express");
    CARD_TYPE_LIST = Collections.unmodifiableList(cardList);
  }

  /* Constructors */

  public OrderBean() {
    this.order = new Order();
    this.shippingAddressRequired = false;
    this.confirmed = false;
  }

  /* JavaBeans Properties */

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public boolean isShippingAddressRequired() {
    return shippingAddressRequired;
  }

  public void setShippingAddressRequired(boolean shippingAddressRequired) {
    this.shippingAddressRequired = shippingAddressRequired;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public List getCreditCardTypes() {
    return CARD_TYPE_LIST;
  }

  public List getOrderList() {
    return orderList;
  }

  public String getPageDirection() {
    return pageDirection;
  }

  public void setPageDirection(String pageDirection) {
    this.pageDirection = pageDirection;
  }

  /* Public Methods */

  public String newOrderForm() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();
    AccountBean accountBean = (AccountBean) sessionMap.get("accountBean");
    CartBean cartBean = (CartBean) sessionMap.get("cartBean");

    clear();
    if (accountBean == null || !accountBean.isAuthenticated()){
      ActionContext.getActionContext().setSimpleMessage("You must sign on before attempting to check out.  Please sign on and try checking out again.");
      return "signon";
    } else if (cartBean != null) {
      // Re-read account from DB at team's request.
      Account account = accountService.getAccount(accountBean.getAccount().getUsername());
      order.initOrder(account, cartBean.getCart());
      return "success";
    } else {
      ActionContext.getActionContext().setSimpleMessage("An order could not be created because a cart could not be found.");
      return "failure";
    }
  }

  public String newOrder() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();

    if (shippingAddressRequired) {
      shippingAddressRequired = false;
      return "shipping";
    } else if (!isConfirmed()) {
      return "confirm";
    } else if (getOrder() != null) {

      orderService.insertOrder(order);

      CartBean cartBean = (CartBean)sessionMap.get("cartBean");
      cartBean.clear();

      ActionContext.getActionContext().setSimpleMessage("Thank you, your order has been submitted.");

      return "success";
    } else {
      ActionContext.getActionContext().setSimpleMessage("An error occurred processing your order (order was null).");
      return "failure";
    }
  }

  public String listOrders() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();
    AccountBean accountBean = (AccountBean) sessionMap.get("accountBean");
    orderList = orderService.getOrdersByUsername(accountBean.getAccount().getUsername());
    return "success";
  }

  public String switchOrderPage() {
    if ("next".equals(pageDirection)) {
      orderList.nextPage();
    } else if ("previous".equals(pageDirection)) {
      orderList.previousPage();
    }
    return "success";
  }


  public String viewOrder() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();
    AccountBean accountBean = (AccountBean) sessionMap.get("accountBean");

    order = orderService.getOrder(orderId);

    if (accountBean.getAccount().getUsername().equals(order.getUsername())) {
      return "success";
    } else {
      order = null;
      ActionContext.getActionContext().setSimpleMessage("You may only view your own orders.");
      return "failure";
    }
  }

  public void reset() {
    shippingAddressRequired = false;
  }

  public void clear() {
    order = new Order();
    orderId = 0;
    shippingAddressRequired = false;
    confirmed = false;
    orderList = null;
    pageDirection = null;
  }

  public void validate() {
    ActionContext ctx = ActionContext.getActionContext();

    if (!this.isShippingAddressRequired()) {
      validateRequiredField(order.getCreditCard(), "FAKE (!) credit card number required.");
      validateRequiredField(order.getExpiryDate(), "Expiry date is required.");
      validateRequiredField(order.getCardType(), "Card type is required.");

      validateRequiredField(order.getShipToFirstName(), "Shipping Info: first name is required.");
      validateRequiredField(order.getShipToLastName(), "Shipping Info: last name is required.");
      validateRequiredField(order.getShipAddress1(), "Shipping Info: address is required.");
      validateRequiredField(order.getShipCity(), "Shipping Info: city is required.");
      validateRequiredField(order.getShipState(), "Shipping Info: state is required.");
      validateRequiredField(order.getShipZip(), "Shipping Info: zip/postal code is required.");
      validateRequiredField(order.getShipCountry(), "Shipping Info: country is required.");

      validateRequiredField(order.getBillToFirstName(), "Billing Info: first name is required.");
      validateRequiredField(order.getBillToLastName(), "Billing Info: last name is required.");
      validateRequiredField(order.getBillAddress1(), "Billing Info: address is required.");
      validateRequiredField(order.getBillCity(), "Billing Info: city is required.");
      validateRequiredField(order.getBillState(), "Billing Info: state is required.");
      validateRequiredField(order.getBillZip(), "Billing Info: zip/postal code is required.");
      validateRequiredField(order.getBillCountry(), "Billing Info: country is required.");
    }

    if (ctx.isSimpleErrorsExist()) {
      order.setBillAddress1(order.getShipAddress1());
      order.setBillAddress2(order.getShipAddress2());
      order.setBillToFirstName(order.getShipToFirstName());
      order.setBillToLastName(order.getShipToLastName());
      order.setBillCity(order.getShipCity());
      order.setBillCountry(order.getShipCountry());
      order.setBillState(order.getShipState());
      order.setBillZip(order.getShipZip());
    }

  }

}
