<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Ocorreu um erro | Gerenciador de Tarefas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/erro.css" />
</head>
<body class="error-page">
    <main class="error-card" aria-labelledby="errorTitle">
        <div class="error-mark" aria-hidden="true">!</div>
        <p class="error-eyebrow">Gerenciador de Tarefas</p>
        <h1 id="errorTitle">${requestScope.errorTitle}</h1>
        <p class="error-message">${requestScope.errorMessage}</p>
        <a class="error-action" href="${pageContext.request.contextPath}/restricted/index.jsf">
            Ir para o início
        </a>
    </main>
</body>
</html>
