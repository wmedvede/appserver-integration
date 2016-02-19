<%--
  ~ Copyright 2016 JBoss Inc
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~       http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: wmedvede
  Date: 2/11/16
  Time: 3:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title></title>
</head>
<body>


<form method="post" action="managerServlet">

  <table>
    <tr>
      <td>name:</td>
      <td><input type="text" name="name"></td>
    </tr>
    <tr>
      <td>jndi:</td>
      <td><input type="text" name="jndi"></td>
    </tr>
    <tr>
      <td>connectionURL:</td>
      <td><input type="text" name="connectionURL"></td>
    </tr>
    <tr>
      <td>driverClass:</td>
      <td><input type="text" name="driverClass"></td>
    </tr>
    <tr>
      <td>datasourceClass:</td>
      <td><input type="text" name="datasourceClass"></td>
    </tr>
    <tr>
      <td>driverName:</td>
      <td><input type="text" name="driverName"></td>
    </tr>
    <tr>
      <td>user:</td>
      <td><input type="text" name="user"></td>
    </tr>
    <tr>
      <td>password:</td>
      <td><input type="text" name="password"></td>
    </tr>
    <tr>
      <td>poolName:</td>
      <td><input type="text" name="poolName"></td>
    </tr>
    <tr>
      <td>enabled</td>
      <td><input type="text" name="enabled"></td>
    </tr>
  </table>

  <select name="operation">
    <option value="create" >Create</option>
    <option value="enable" >Enable</option>
    <option value="update"/>Update</option>
    <option value="delete"/>Delete</option>
    <option value="verify"/>Verify</option>
    <option value="list"/>List</option>
  </select>

  <input type="submit"/>

</form>
</body>
</html>
