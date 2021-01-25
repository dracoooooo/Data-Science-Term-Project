from time import sleep

from flask import Flask, jsonify, request
from flask_cors import CORS
import src.code_recommender.code_recommender as cr


DEBUG = True

app = Flask(__name__)
app.config.from_object(__name__)

# enable CORS
CORS(app, resources={r'/*': {'origins': '*'}})


@app.route('/lang/', methods=['POST'])
def lang():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        lang, prob = cr.get_lang(code)
        return lang


@app.route('/func/', methods=['POST'])
def func():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        func, prob = cr.get_func(code)
        return lang


@app.route('/LandF/', methods=['POST'])
def LandF():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        lang, lang_prob = cr.get_lang(code)
        func, func_prob = cr.get_func(code)
        return {'lang': lang, 'func': func, 'lang_prob': str(lang_prob), 'func_prob': str(func_prob)}


@app.route('/getcode/', methods=['POST'])
def get3code():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        ret = cr.recommend_code(code)
        print(ret)
        recommends = {}
        recommend = {}
        if ret == 0:
            return "not support this language"
        else:
            for i in range(ret.__len__()):
                recommend["name"] = ret[i][0],
                recommend["code"] = ret[i][1],
                recommend["similarity"] = ret[i][2],
                recommend["quality"] = ret[i][3]
                recommends['r' + str(i)] = recommend.copy()
            print(recommends)
            return recommends


if __name__ == "__main__":
    # ret = cr.recommend_code("dfajklshgksdfg")
    # print(ret)
    app.run()