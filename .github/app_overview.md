## iCodeBetter 
iCodeBetter is a Spring Boot application. So you need to know some Spring concepts in order to understand codebase better. In this guide we
will give detailed overview of backend and frontend of iCodeBetter :bowtie:

<hr/>

### Backend

<hr/>

#### Controller

*AppServet.java* handles ajax requests. Most common requests are `showPage` , `showForm` , `ajaxQueryData` , `ajaxPostForm` , 
`ajaxExecDbFunc` . So this how it handles these requests:

* `showPage`  

It handles showPage request by calling Framework engine's `getTemplateResult` function and returning template code as response.  

* `showForm`  

It calls Framework engine's `getFormResult` function and returns that form result as response.  

* `ajaxQueryData`  

It calls Framework engine's `executeQuery` function and returns query result as response.  

* `ajaxPostForm`  

It calls Framework engine's `postForm4Table` function and returns form result as response.  

* `ajaxExecDbFunc`

It calls Framework engine's `executeQuery` function and returns result as response.


#### Framework engine

#### UI adapter


<hr/>
