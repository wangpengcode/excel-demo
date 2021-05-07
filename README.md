# kotlin excel demo
a excel demo in kotlin

1. when you want download excel, just do 

`@GetMapping("/download")
   	fun download(): ResponseEntity<ByteArray> {
   		
   		val list = listOf(
   			A("123", BigDecimal.TEN),
   			A("778", BigDecimal.TEN)
   		)
   		val a = writeToExcel(list)
   		
   		
   		return excelResponseEntity(
   			LocalDateTime.now().toString(), a
   		)
   	}`
 
2. when you want upload excel, just do
 
`@PostMapping("/upload")
  	fun uploadFile(request: MultipartHttpServletRequest): ResponseEntity<String> {
  		val file = request.getFiles("file")[0]
  		val list = excelToList(
  			file.inputStream,
  			Template::class
  		)
  		list?.forEach(::println)
  		return ResponseEntity.status(HttpStatus.OK).body("$list")
  	}`

 you need the demo.xlsx in the patch [doc], also, when you post the request, you should set 
 the body's type as Multipart, and select the file then upload.