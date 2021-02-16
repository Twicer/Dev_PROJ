<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta charset="utf-8" />
    <base href="/static/" />
    <link rel="icon" href="favicon.ico" />
    <link rel="preconnect" href="https://fonts.gstatic.com" />
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@100;200;300;400;600&display=swap" rel="stylesheet" />

    <meta name="viewport" content="width=device-width" />
    <meta name="theme-color" content="#000000" />
    <meta name="description" content="WiseMapping" />
    <link rel="apple-touch-icon" href="favicon.png" />
    <link rel="manifest" href="manifest.json" />
    <title>Loading | WiseMapping</title>

    <script>
        window.serverconfig = { apiBaseUrl: 'http://localhost:8080' };
    </script>

</head>

<body>
    <noscript>You need to enable JavaScript to run this app.</noscript>
    <div id="root"></div>
    <script src="vendors.bundle.js"></script>
    <script src="app.bundle.js"></script>
</body>

</html>