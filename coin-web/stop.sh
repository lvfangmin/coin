ps axu | grep coin-web | grep -v grep | awk '{print $2}' | xargs kill
