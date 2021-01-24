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


@app.route('/get3code/', methods=['POST'])
def get3code():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        ret = cr.recommend_code(code)
        recommends = []
        for i in range(ret.__len__()):
            recommends.append(
                {
                    "name": ret[i][0],
                    "code": ret[i][1],
                    "similarity": ret[i][2],
                    "qualiry": ret[i][3]
                }
            )
        ret = {}
        for i in range(recommends.__len__()):
            ret["r" + i] = recommends[i]
        print(ret)
        return ret


if __name__ == "__main__":
    ret = cr.recommend_code("dfajklshgksdfg")
    print(ret)
    # app.run()