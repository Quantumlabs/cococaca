Resource request transaction handling guide line
---
Any resource handling should only implement its AcceptableResponse and IResouceHandler.

---
###Resource model-ing
Design the REST-full API which you want to use for querying specific resource.
Specify the resource hierarchy.

---
###Implement IResourceHandler
1.Implement ``accept`` method which needs to determine whether the fragment is acceptable for handling.
2.In case the the fragment is not the tail one and is acceptable for current resource, some more constrains need to be done.
Implement ``IResourceHandler``, notify the ``IResourceHanderCallBack`` once handling is done, ``HTTPServletResponseBasedCallBack`` the default ``IResourceHandlerCallBack`` implementation will write result to output stream of the HTTP response, in case the default ``HTTPServletResponseBasedCallBack`` is not able to achieve the requirement.
