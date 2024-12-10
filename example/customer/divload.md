## view 페이지 div 안에 출력

- [load 문서](https://api.jquery.com/load/)
- [callback 참고](https://www.w3schools.com/jquery/jquery_ajax_load.asp)

현재 load 후 페이지가 이동되는 문제가 있음.
추가 확인 필요

    ```bash
    function importPage(url){
        $('#hjkim').load(url, function(responseText, statusTxt, xhr){
            alert(statusTxt);
            console.log(xhr.status +":"+ JSON.stringify(xhr));
        });
    }
    ```