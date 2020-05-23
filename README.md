> 单点登录（Single Sign On），简称为SSO，是比较流行的企业业务整合的解决方案之一。 SSO的定义是在多个应用系统中，用户只需要登录一次就可以访问所有相互信任的应用系统。

[SOSExample](https://github.com/asche910/SOSExample)  是使用SpringBoot框架实现的一个单点登录简单demo，主要为功能即在某个子系统登录或者退出登录后，在其余的子系统中也表现出相同的状态。



### 项目思路
本项目包含两个模块，一个是负责统一登录认证的模块，另一个即正常业务模块。业务模块使用多个不同端口启动来模仿多个子系统。某个子系统登录时，将相应的登录信息发送给业务服务器，业务服务器再将信息传给统一认证服务器，成功之后，返回浏览器，同时浏览器也会使用Ajax发送异步请求，向其余的子系统发送相应信息（可以不包括密码），然后其余子系统返回结果中包含Set-Cookie（这里可以只返回个用户名），浏览器收到后会在其余子系统中添加相应的Cookie。有了Cookie，事情就应该好办了。后面详细的可以参考代码。




### 关键源码
业务服务器中的登录接口，主要负责接收浏览器登录信息，然后交给认证服务器去认证，登录成功则在当前Session中标记一下
```java
    @PostMapping("/login")
    public String login(String username, String password,
                        HttpServletRequest request, Model model) throws IOException {
        String url = String.format("http://localhost:9090/login?username=%s&password=%s", username, password);
        String responseContent = HttpUtils.getResponseContent(url);
        System.out.println("login:" + responseContent);
        if("1".equals(responseContent)){
            request.getSession().setAttribute("user", username);
            return "redirect:home";
        }
        model.addAttribute("msg", "login failed!");
        return "login";
    }
```
这里只是主要用于浏览器异步请求的时候为子系统添加Cookie，作为demo，没有考虑太多安全因素。
```java
    @RequestMapping("/addCookie")
    @ResponseBody
    public String addCookie(String cookie, HttpServletResponse response){
        response.addCookie(new Cookie("user", cookie));
        return "1";
    }
```
这里是注销登录，主要包含清除当前Session中的标记，以及认证服务端也清除登录标记。
```java
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        System.out.println(user);
        if (user != null){
            String url = String.format("http://localhost:9090/logout?username=%s", user.toString());
            HttpUtils.getResponseContent(url);
            session.setAttribute("user", null);
        }
        return "redirect:home";
    }
```

然后就是统一认证服务端代码了，代码很少，就不讲了。
```java
@Controller
public class LoginController {

    private static Set<String> userSet = new HashSet<>();

    @Autowired
    LoginMapper loginMapper;

    @GetMapping("/login")
    @ResponseBody
    public String login(String username, String password){
        Login login = loginMapper.selectByPrimaryKey(username);
        userSet.add(username);
        return login != null && login.getPassword().equals(password) ? "1" : "0";
    }

    @GetMapping("/loginCheck")
    @ResponseBody
    public String checkLogin(String username){
        return userSet.contains(username) ? "1" : "0";
    }

    @GetMapping("/logout")
    @ResponseBody
    public String logout(String username){
        return userSet.remove(username) ? "1" : "0";
    }
}
```

前端登录js部分代码
```js
<script th:inline="javascript">
    /*<![CDATA[*/

    var url1 = "http://localhost:8080/addCookie?cookie=";
    var url2 = "http://localhost:8081/addCookie?cookie=";

    function login(){
        var user = $("#username").val()

        $("form#login-form").submit();

        $.ajax({
            url: url1 + user,
            type: "get",
            dataType: "jsonp"
        })
        $.ajax({
            url: url2 + user,
            type: "get",
            dataType: "jsonp"
        })
        // alert(form)
    }
</script>
```


然后数据库的话，这里附上SQL语句，主要是user和password
```sql
create table login
(
    user     varchar(12)             not null,
    password varchar(16) default '0' null,
    name     varchar(20)             null,
    constraint login_user_uindex
        unique (user)
)
    charset = utf8;

alter table login
    add primary key (user);
```

最后完整源码上传到了Github：[SOSExample](https://github.com/asche910/SOSExample) 