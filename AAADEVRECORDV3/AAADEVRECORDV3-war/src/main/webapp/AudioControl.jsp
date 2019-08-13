<%-- 
    Document   : AudioControl
    Created on : Aug 12, 2019, 4:31:58 PM
    Author     : umansilla
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <%@include file="/templates/header.jsp" %>
    <body>
        <%@include file="/templates/navbar.jsp" %>
        <%@include file="/templates/jumbotron.jsp" %>
        <div class="container">
            <div class="col-sm-12">
                <button type="button" class="btn btn-primary btn-block" id="createDirectory">Create New Directory</button>
            </div>  
        </div>
        <hr>
        <div class="container" id="principalContainer">

        </div>
        <script src="js/AudioControl.js"></script>
        <script src="js/sweetAlertmin.js"></script>
    </body>

</html>
