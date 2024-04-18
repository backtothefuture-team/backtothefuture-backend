import requests

def save_openapi_docs(url, filename):
    # API 문서를 가져옵니다.
    response = requests.get(url)
    if response.status_code == 200:
        # 응답으로부터 받은 내용을 파일로 저장합니다.
        with open(filename, 'w') as file:
            file.write(response.text)
        print(f'API 문서가 성공적으로 저장되었습니다: {filename}')
    else:
        print(f'API 문서를 가져오는데 실패했습니다. 상태 코드: {response.status_code}')

api_url = 'http://localhost:8080/v1/v3/api-docs.yaml'
output_file = './openapi/member-openapi.yaml'
save_openapi_docs(api_url, output_file)

api_url = 'http://localhost:8081/v1/v3/api-docs.yaml'
output_file = './openapi/store-openapi.yaml'
save_openapi_docs(api_url, output_file)

api_url = 'http://localhost:8084/v1/v3/api-docs.yaml'
output_file = './openapi/event-openapi.yaml'
save_openapi_docs(api_url, output_file)
