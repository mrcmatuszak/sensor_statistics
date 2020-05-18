build:
	./mill app.reformat && \
		./mill app.test.reformat && \
		./mill app.compile && \
		./mill app.test
run:
	./mill app.run