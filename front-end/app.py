from dash import Dash, html, dcc, callback, Input, Output, State
from html import unescape
import requests

back_end_api_url = "http://localhost:8080"

app = Dash()

#-------------interacting functions with back end API-------------------
#GET request: retrieves question and possible answers
def retrieve_question():
    answer_list = requests.get(back_end_api_url+"/questions").json()

    for i in range(len(answer_list)):
        answer_list[i] = unescape(answer_list[i])

    question_text = answer_list.pop(-1)
    return question_text, answer_list

#POST request: checks correctness of an answer
def check_answer(answer:str):
    correctness = requests.post(back_end_api_url+"/checkanswers", data=answer)
    return correctness.text


#---------------callback functions for front-end----------------------------
@callback(
    Output("question","children"),
    Output("answers", "options"),
    Output("correctness-indicator","children", allow_duplicate=True),
    Input("next-button","n_clicks"),
    prevent_initial_call='initial_duplicate'
)
def display_new_question(_):
    question, answers = retrieve_question()
    return question, answers, ""


@callback(
    Output("correctness-indicator","children", allow_duplicate=True),
    State("answers","value"),
    Input("submit-button","n_clicks"),
    prevent_initial_call=True
)
def display_correctness(answer, _):
    correctness = check_answer(answer)
    return correctness

#--------------------app layout---------------------------------------------
app.layout = html.Div(children=[
    html.H1(id="question"),
    dcc.RadioItems(id="answers"),
    html.Caption(children="", id="correctness-indicator"),
    html.Button("Submit Answer", id="submit-button"),
    html.Button("Next Question", id="next-button")
])


if __name__ == '__main__':
    app.run(debug=False)
