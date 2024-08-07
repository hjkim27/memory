#### dwr로 사용중이던 ajax 제거 후 변경
  1) 값 전달 오류 : list 로 된 값이 jquery 에서 controller 로 전달되지 않음 > dataType: "json" 추가
  2) url 오류 : 호출 url에서 shpc 가 같이 출력되지 않음 > ${contextPath} 추가
  3) return 오류 : controller 에서 값을 반환하지 못해 error 로 던져짐 > @ResponseBody, @RequestParam 추가


*** 기존 ** : service.메서드 바로 호출
```script
ajaxClientService.deleteClients(clientId, {
    callback: function (data) {
        if ( clientId.length == data ) {
            alert("선택된 사용자를 삭제하였습니다.");
        }
        else if ( data == 0 ) {
            alert("사용자 삭제에 실패하였습니다.");
        }
        else {
             alert("일부 사용자를 삭제하지 못했습니다.");
        }
        showClientList();
    },
    async: false
});
```

*** 수정 ***

** controller 
```java
@ResponseBody
@RequestMapping(value = "/dwr/deleteClients.ctr", method = RequestMethod.POST)
public Integer deleteClients(HttpServletRequest request, @RequestParam("clientId[]") List<Integer> clientId)  {
    int result = pcScanClientService.deleteClients(request, clientId);
    return result;
}
```
** ajax
```script
jQuery.ajax({
    url: "${contextPath}/dwr/deleteClients.ctr",
    type: "POST",
    async: false,
    data: {clientId: clientId},
    dataType: "json",
    success: function(data){
        if ( clientId.length == data ) {
            alert("선택된 사용자를 삭제하였습니다.");
        }
        else if ( data == 0 ) {
            alert("사용자 삭제에 실패하였습니다.");
        }
        else {
            alert("일부 사용자를 삭제하지 못했습니다.");
        }
        showClientList();
    },
    error: function(e){
        console.log(e);
    }
});
```
